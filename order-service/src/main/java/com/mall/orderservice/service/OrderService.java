import com.mall.orderservice.model.Order;
import com.mall.orderservice.model.OrderItem;
import com.mall.orderservice.model.OrderStatusHistory;
import com.mall.orderservice.repository.OrderRepository;
import com.mall.orderservice.repository.OrderStatusHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ProductService productService;  // 需要实现，用于调用商品服务
    private final UserService userService;        // 需要实现，用于调用用户服务

    // 创建订单
    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        // 1. 验证用户
        validateUser(request.getUserId());
        
        // 2. 验证商品
        List<OrderItem> orderItems = validateAndCreateOrderItems(request.getItems());
        
        // 3. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId());
        order.setUserName(request.getUserName());
        order.setStatus(Order.OrderStatus.PENDING_PAYMENT);
        order.setPaymentType(request.getPaymentType());
        order.setPaymentStatus(Order.PaymentStatus.UNPAID);
        
        // 4. 设置订单金额
        calculateOrderAmount(order, orderItems, request);
        
        // 5. 设置收货信息
        setReceiverInfo(order, request);
        
        // 6. 保存订单
        order.setOrderItems(orderItems);
        order = orderRepository.save(order);
        
        // 7. 记录状态历史
        createStatusHistory(order, null, Order.OrderStatus.PENDING_PAYMENT, 
                request.getUserId(), request.getUserName(), "创建订单", 
                OrderStatusHistory.OperationType.CREATE);
        
        // 8. 扣减库存
        productService.decreaseStock(orderItems);
        
        return order;
    }

    // 更新订单状态
    @Transactional
    @CacheEvict(value = "order", key = "#orderId")
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus, 
            Long operatorId, String operatorName, String remark) {
        Order order = getOrder(orderId);
        Order.OrderStatus oldStatus = order.getStatus();
        
        // 验证状态转换是否合法
        validateStatusTransition(oldStatus, newStatus);
        
        // 更新订单状态
        order.setStatus(newStatus);
        order = orderRepository.save(order);
        
        // 记录状态历史
        createStatusHistory(order, oldStatus, newStatus, operatorId, operatorName, 
                remark, getOperationType(newStatus));
        
        // 处理状态变更后的业务逻辑
        handleStatusChange(order, oldStatus, newStatus);
        
        return order;
    }

    // 取消订单
    @Transactional
    @CacheEvict(value = "order", key = "#orderId")
    public Order cancelOrder(Long orderId, Long userId, String reason) {
        Order order = getOrder(orderId);
        
        // 验证订单是否可以取消
        if (!canCancel(order)) {
            throw new IllegalStateException("订单当前状态不允许取消");
        }
        
        // 验证操作权限
        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("无权操作此订单");
        }
        
        // 更新订单状态
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        order = orderRepository.save(order);
        
        // 记录状态历史
        createStatusHistory(order, Order.OrderStatus.PENDING_PAYMENT, 
                Order.OrderStatus.CANCELLED, userId, "用户", reason, 
                OrderStatusHistory.OperationType.CANCEL);
        
        // 恢复库存
        productService.increaseStock(order.getOrderItems());
        
        return order;
    }

    // 获取订单详情
    @Transactional(readOnly = true)
    @Cacheable(value = "order", key = "#orderId")
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("订单不存在"));
    }

    // 获取订单列表
    @Transactional(readOnly = true)
    public Page<Order> getOrders(Long userId, Order.OrderStatus status, 
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (startTime != null && endTime != null) {
            return orderRepository.findByUserIdAndCreatedAtBetween(userId, startTime, endTime, pageable);
        } else {
            return orderRepository.findByUserId(userId, pageable);
        }
    }

    // 定时任务：处理超时订单
    @Scheduled(fixedRate = 60000)  // 每分钟执行一次
    @Transactional
    public void handleTimeoutOrders() {
        // 处理待支付超时订单
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);  // 30分钟超时
        List<Order> timeoutOrders = orderRepository.findTimeoutOrders(timeoutTime);
        for (Order order : timeoutOrders) {
            cancelOrder(order.getId(), order.getUserId(), "支付超时自动取消");
        }
        
        // 处理待收货超时订单
        LocalDateTime deliveryTimeoutTime = LocalDateTime.now().minusDays(7);  // 7天超时
        List<Order> deliveryTimeoutOrders = orderRepository.findDeliveryTimeoutOrders(deliveryTimeoutTime);
        for (Order order : deliveryTimeoutOrders) {
            updateOrderStatus(order.getId(), Order.OrderStatus.COMPLETED, 
                    null, "系统", "自动确认收货", OrderStatusHistory.OperationType.SYSTEM);
        }
    }

    // 生成订单编号
    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    // 验证用户
    private void validateUser(Long userId) {
        if (!userService.existsById(userId)) {
            throw new EntityNotFoundException("用户不存在");
        }
    }

    // 验证并创建订单项
    private List<OrderItem> validateAndCreateOrderItems(List<OrderItemRequest> items) {
        // 实现商品验证和订单项创建逻辑
        return null;  // TODO: 实现具体逻辑
    }

    // 计算订单金额
    private void calculateOrderAmount(Order order, List<OrderItem> items, OrderCreateRequest request) {
        // 实现订单金额计算逻辑
    }

    // 设置收货信息
    private void setReceiverInfo(Order order, OrderCreateRequest request) {
        // 实现收货信息设置逻辑
    }

    // 创建状态历史记录
    private void createStatusHistory(Order order, Order.OrderStatus fromStatus, 
            Order.OrderStatus toStatus, Long operatorId, String operatorName, 
            String remark, OrderStatusHistory.OperationType operationType) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setOperatorId(String.valueOf(operatorId));
        history.setOperator(operatorName);
        history.setRemark(remark);
        history.setOperationType(operationType);
        statusHistoryRepository.save(history);
    }

    // 验证状态转换是否合法
    private void validateStatusTransition(Order.OrderStatus fromStatus, Order.OrderStatus toStatus) {
        // 实现状态转换验证逻辑
    }

    // 获取操作类型
    private OrderStatusHistory.OperationType getOperationType(Order.OrderStatus status) {
        // 实现操作类型判断逻辑
        return null;  // TODO: 实现具体逻辑
    }

    // 处理状态变更后的业务逻辑
    private void handleStatusChange(Order order, Order.OrderStatus oldStatus, Order.OrderStatus newStatus) {
        // 实现状态变更后的业务处理逻辑
    }

    // 检查订单是否可以取消
    private boolean canCancel(Order order) {
        return order.getStatus() == Order.OrderStatus.PENDING_PAYMENT;
    }

    /**
     * 通过订单号获取订单
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "order", key = "#orderNo")
    public Order getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }
} 