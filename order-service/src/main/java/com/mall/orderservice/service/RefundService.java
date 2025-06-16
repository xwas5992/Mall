

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.orderservice.dto.RefundRequest;
import com.mall.orderservice.exception.BusinessException;
import com.mall.orderservice.model.Order;
import com.mall.orderservice.model.RefundItem;
import com.mall.orderservice.model.RefundOrder;
import com.mall.orderservice.model.RefundStatusHistory;
import com.mall.orderservice.repository.RefundOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundOrderRepository refundOrderRepository;
    private final OrderService orderService;
    private final WxPayService wxPayService;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    /**
     * 申请退款
     */
    @Transactional
    public RefundOrder applyRefund(Long userId, RefundRequest request) {
        log.info("申请退款 - 用户ID: {}, 订单ID: {}", userId, request.getOrderId());

        // 1. 获取订单信息
        Order order = orderService.getOrder(request.getOrderId());
        
        // 2. 验证订单状态
        if (!canRefund(order)) {
            throw new BusinessException("INVALID_ORDER_STATUS", "当前订单状态不允许退款");
        }
        
        // 3. 验证用户权限
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "无权操作此订单");
        }
        
        // 4. 检查是否有未完成的退款申请
        if (refundOrderRepository.existsUnfinishedRefund(order.getId())) {
            throw new BusinessException("REFUND_EXISTS", "该订单已有未完成的退款申请");
        }
        
        // 5. 创建退款订单
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundNo(generateRefundNo());
        refundOrder.setOrderId(order.getId());
        refundOrder.setOrderNo(order.getOrderNo());
        refundOrder.setUserId(order.getUserId());
        refundOrder.setUserName(order.getUserName());
        refundOrder.setRefundType(request.getRefundType());
        refundOrder.setStatus(RefundOrder.RefundStatus.PENDING_APPROVE);
        refundOrder.setReason(request.getReason());
        refundOrder.setDescription(request.getDescription());
        refundOrder.setApplyTime(LocalDateTime.now());
        
        // 6. 创建退款商品
        List<RefundItem> refundItems = new ArrayList<>();
        BigDecimal totalRefundAmount = BigDecimal.ZERO;
        
        for (RefundRequest.RefundItemRequest itemRequest : request.getItems()) {
            // 查找订单项
            Order.OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemRequest.getOrderItemId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("ORDER_ITEM_NOT_FOUND", "订单项不存在"));
            
            // 验证退款数量
            if (itemRequest.getQuantity() > orderItem.getQuantity()) {
                throw new BusinessException("INVALID_QUANTITY", "退款数量不能大于购买数量");
            }
            
            // 创建退款商品
            RefundItem refundItem = new RefundItem();
            refundItem.setRefundOrder(refundOrder);
            refundItem.setOrderItemId(orderItem.getId());
            refundItem.setProductId(orderItem.getProductId());
            refundItem.setProductName(orderItem.getProductName());
            refundItem.setProductSku(orderItem.getProductSku());
            refundItem.setProductImage(orderItem.getProductImage());
            refundItem.setQuantity(itemRequest.getQuantity());
            refundItem.setUnitPrice(orderItem.getUnitPrice());
            refundItem.setTotalPrice(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            refundItem.setProperties(orderItem.getProperties());
            refundItem.setReason(itemRequest.getReason());
            refundItem.setDescription(itemRequest.getDescription());
            
            try {
                if (itemRequest.getImages() != null) {
                    refundItem.setImages(objectMapper.writeValueAsString(itemRequest.getImages()));
                }
            } catch (Exception e) {
                log.error("序列化退款图片失败", e);
            }
            
            refundItems.add(refundItem);
            totalRefundAmount = totalRefundAmount.add(refundItem.getTotalPrice());
        }
        
        refundOrder.setRefundAmount(totalRefundAmount);
        refundOrder.setRefundItems(refundItems);
        
        // 7. 保存退款订单
        refundOrder = refundOrderRepository.save(refundOrder);
        
        // 8. 记录状态历史
        createStatusHistory(refundOrder, null, RefundOrder.RefundStatus.PENDING_APPROVE,
                String.valueOf(userId), order.getUserName(), "申请退款",
                RefundStatusHistory.OperationType.APPLY);
        
        // 9. 更新订单状态
        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.REFUNDING,
                null, String.valueOf(userId), "申请退款", OrderStatusHistory.OperationType.REFUND);
        
        return refundOrder;
    }

    /**
     * 审核退款申请
     */
    @Transactional
    public RefundOrder approveRefund(Long refundId, String operatorId, String operatorName, String remark) {
        log.info("审核退款申请 - 退款单ID: {}, 操作人: {}", refundId, operatorName);
        
        RefundOrder refundOrder = getRefundOrder(refundId);
        
        // 验证状态
        if (refundOrder.getStatus() != RefundOrder.RefundStatus.PENDING_APPROVE) {
            throw new BusinessException("INVALID_STATUS", "当前状态不允许审核");
        }
        
        // 更新状态
        refundOrder.setStatus(RefundOrder.RefundStatus.APPROVED);
        refundOrder.setApproveTime(LocalDateTime.now());
        refundOrder.setOperatorId(operatorId);
        refundOrder.setOperatorName(operatorName);
        refundOrder = refundOrderRepository.save(refundOrder);
        
        // 记录状态历史
        createStatusHistory(refundOrder, RefundOrder.RefundStatus.PENDING_APPROVE,
                RefundOrder.RefundStatus.APPROVED, operatorId, operatorName, remark,
                RefundStatusHistory.OperationType.APPROVE);
        
        // 如果是仅退款，直接进入退款流程
        if (refundOrder.getRefundType() == RefundOrder.RefundType.REFUND_ONLY) {
            return processRefund(refundId, operatorId, operatorName);
        }
        
        return refundOrder;
    }

    /**
     * 拒绝退款申请
     */
    @Transactional
    public RefundOrder rejectRefund(Long refundId, String operatorId, String operatorName, String reason) {
        log.info("拒绝退款申请 - 退款单ID: {}, 操作人: {}", refundId, operatorName);
        
        RefundOrder refundOrder = getRefundOrder(refundId);
        
        // 验证状态
        if (refundOrder.getStatus() != RefundOrder.RefundStatus.PENDING_APPROVE) {
            throw new BusinessException("INVALID_STATUS", "当前状态不允许拒绝");
        }
        
        // 更新状态
        refundOrder.setStatus(RefundOrder.RefundStatus.REJECTED);
        refundOrder.setRejectTime(LocalDateTime.now());
        refundOrder.setRejectReason(reason);
        refundOrder.setOperatorId(operatorId);
        refundOrder.setOperatorName(operatorName);
        refundOrder = refundOrderRepository.save(refundOrder);
        
        // 记录状态历史
        createStatusHistory(refundOrder, RefundOrder.RefundStatus.PENDING_APPROVE,
                RefundOrder.RefundStatus.REJECTED, operatorId, operatorName, reason,
                RefundStatusHistory.OperationType.REJECT);
        
        // 恢复订单状态
        Order order = orderService.getOrder(refundOrder.getOrderId());
        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.COMPLETED,
                null, operatorId, "退款申请被拒绝", OrderStatusHistory.OperationType.REFUND);
        
        return refundOrder;
    }

    /**
     * 提交退货信息
     */
    @Transactional
    public RefundOrder submitReturnInfo(Long refundId, Long userId, String logisticsCompany, String logisticsNo) {
        log.info("提交退货信息 - 退款单ID: {}, 用户ID: {}", refundId, userId);
        
        RefundOrder refundOrder = getRefundOrder(refundId);
        
        // 验证状态
        if (refundOrder.getStatus() != RefundOrder.RefundStatus.APPROVED) {
            throw new BusinessException("INVALID_STATUS", "当前状态不允许提交退货信息");
        }
        
        // 验证用户权限
        if (!refundOrder.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "无权操作此退款单");
        }
        
        // 更新状态
        refundOrder.setStatus(RefundOrder.RefundStatus.RETURNED);
        refundOrder.setReturnTime(LocalDateTime.now());
        refundOrder.setLogisticsCompany(logisticsCompany);
        refundOrder.setLogisticsNo(logisticsNo);
        refundOrder = refundOrderRepository.save(refundOrder);
        
        // 记录状态历史
        createStatusHistory(refundOrder, RefundOrder.RefundStatus.APPROVED,
                RefundOrder.RefundStatus.RETURNED, String.valueOf(userId), refundOrder.getUserName(),
                "提交退货信息", RefundStatusHistory.OperationType.RETURN);
        
        return refundOrder;
    }

    /**
     * 确认收货
     */
    @Transactional
    public RefundOrder confirmReceive(Long refundId, String operatorId, String operatorName) {
        log.info("确认收货 - 退款单ID: {}, 操作人: {}", refundId, operatorName);
        
        RefundOrder refundOrder = getRefundOrder(refundId);
        
        // 验证状态
        if (refundOrder.getStatus() != RefundOrder.RefundStatus.RETURNED) {
            throw new BusinessException("INVALID_STATUS", "当前状态不允许确认收货");
        }
        
        // 更新状态
        refundOrder.setStatus(RefundOrder.RefundStatus.RECEIVED);
        refundOrder.setReceiveTime(LocalDateTime.now());
        refundOrder.setOperatorId(operatorId);
        refundOrder.setOperatorName(operatorName);
        refundOrder = refundOrderRepository.save(refundOrder);
        
        // 记录状态历史
        createStatusHistory(refundOrder, RefundOrder.RefundStatus.RETURNED,
                RefundOrder.RefundStatus.RECEIVED, operatorId, operatorName,
                "确认收货", RefundStatusHistory.OperationType.RECEIVE);
        
        // 处理退款
        return processRefund(refundId, operatorId, operatorName);
    }

    /**
     * 处理退款
     */
    @Transactional
    public RefundOrder processRefund(Long refundId, String operatorId, String operatorName) {
        log.info("处理退款 - 退款单ID: {}, 操作人: {}", refundId, operatorName);
        
        RefundOrder refundOrder = getRefundOrder(refundId);
        
        // 验证状态
        if (refundOrder.getStatus() != RefundOrder.RefundStatus.APPROVED &&
            refundOrder.getStatus() != RefundOrder.RefundStatus.RECEIVED) {
            throw new BusinessException("INVALID_STATUS", "当前状态不允许退款");
        }
        
        try {
            // 调用退款接口
            Map<String, Object> refundResult = wxPayService.refund(
                refundOrder.getOrderNo(),
                refundOrder.getRefundNo(),
                refundOrder.getRefundAmount()
            );
            
            // 更新状态
            refundOrder.setStatus(RefundOrder.RefundStatus.REFUNDED);
            refundOrder.setRefundTime(LocalDateTime.now());
            refundOrder.setOperatorId(operatorId);
            refundOrder.setOperatorName(operatorName);
            refundOrder = refundOrderRepository.save(refundOrder);
            
            // 记录状态历史
            createStatusHistory(refundOrder, refundOrder.getStatus(),
                    RefundOrder.RefundStatus.REFUNDED, operatorId, operatorName,
                    "退款成功", RefundStatusHistory.OperationType.REFUND);
            
            // 更新订单状态
            Order order = orderService.getOrder(refundOrder.getOrderId());
            orderService.updateOrderStatus(order.getId(), Order.OrderStatus.REFUNDED,
                    null, operatorId, "退款成功", OrderStatusHistory.OperationType.REFUND);
            
            // 恢复库存
            if (refundOrder.getRefundType() == RefundOrder.RefundType.REFUND_RETURN) {
                productService.increaseStock(refundOrder.getRefundItems().stream()
                    .map(item -> {
                        Order.OrderItem orderItem = new Order.OrderItem();
                        orderItem.setProductId(item.getProductId());
                        orderItem.setProductSku(item.getProductSku());
                        orderItem.setQuantity(item.getQuantity());
                        return orderItem;
                    })
                    .collect(Collectors.toList()));
            }
            
            return refundOrder;
        } catch (Exception e) {
            log.error("退款失败", e);
            throw new BusinessException("REFUND_FAILED", "退款失败：" + e.getMessage());
        }
    }

    /**
     * 取消退款
     */
    @Transactional
    public RefundOrder cancelRefund(Long refundId, Long userId) {
        log.info("取消退款 - 退款单ID: {}, 用户ID: {}", refundId, userId);
        
        RefundOrder refundOrder = getRefundOrder(refundId);
        
        // 验证状态
        if (!canCancel(refundOrder)) {
            throw new BusinessException("INVALID_STATUS", "当前状态不允许取消");
        }
        
        // 验证用户权限
        if (!refundOrder.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "无权操作此退款单");
        }
        
        // 更新状态
        refundOrder.setStatus(RefundOrder.RefundStatus.CANCELLED);
        refundOrder = refundOrderRepository.save(refundOrder);
        
        // 记录状态历史
        createStatusHistory(refundOrder, refundOrder.getStatus(),
                RefundOrder.RefundStatus.CANCELLED, String.valueOf(userId), refundOrder.getUserName(),
                "取消退款", RefundStatusHistory.OperationType.CANCEL);
        
        // 恢复订单状态
        Order order = orderService.getOrder(refundOrder.getOrderId());
        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.COMPLETED,
                null, String.valueOf(userId), "退款已取消", OrderStatusHistory.OperationType.REFUND);
        
        return refundOrder;
    }

    /**
     * 获取退款订单
     */
    @Transactional(readOnly = true)
    public RefundOrder getRefundOrder(Long refundId) {
        return refundOrderRepository.findById(refundId)
            .orElseThrow(() -> new BusinessException("REFUND_NOT_FOUND", "退款单不存在"));
    }

    /**
     * 获取退款订单列表
     */
    @Transactional(readOnly = true)
    public Page<RefundOrder> getRefundOrders(Long userId, RefundOrder.RefundStatus status,
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        if (status != null) {
            return refundOrderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (startTime != null && endTime != null) {
            return refundOrderRepository.findByCreatedAtBetween(startTime, endTime, pageable);
        } else {
            return refundOrderRepository.findByUserId(userId, pageable);
        }
    }

    /**
     * 生成退款单号
     */
    private String generateRefundNo() {
        return "RF" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 检查订单是否可以退款
     */
    private boolean canRefund(Order order) {
        return order.getStatus() == Order.OrderStatus.COMPLETED ||
               order.getStatus() == Order.OrderStatus.PENDING_RECEIVE;
    }

    /**
     * 检查退款单是否可以取消
     */
    private boolean canCancel(RefundOrder refundOrder) {
        return refundOrder.getStatus() == RefundOrder.RefundStatus.PENDING_APPROVE ||
               refundOrder.getStatus() == RefundOrder.RefundStatus.APPROVED;
    }

    /**
     * 创建状态历史记录
     */
    private void createStatusHistory(RefundOrder refundOrder, RefundOrder.RefundStatus fromStatus,
            RefundOrder.RefundStatus toStatus, String operatorId, String operatorName,
            String remark, RefundStatusHistory.OperationType operationType) {
        RefundStatusHistory history = new RefundStatusHistory();
        history.setRefundOrder(refundOrder);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setOperatorId(operatorId);
        history.setOperator(operatorName);
        history.setRemark(remark);
        history.setOperationType(operationType);
        refundOrder.getStatusHistory().add(history);
    }
} 