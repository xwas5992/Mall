

import com.mall.orderservice.model.Order;
import com.mall.orderservice.model.OrderStatusHistory;
import com.mall.orderservice.repository.OrderRepository;
import com.mall.orderservice.service.OrderService;
import com.mall.orderservice.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusCheckTask {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final WxPayService wxPayService;

    /**
     * 每5分钟检查一次未支付订单的状态
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkPaymentStatus() {
        log.info("开始检查未支付订单状态");
        
        // 1. 查询待支付订单
        LocalDateTime checkTime = LocalDateTime.now().minusMinutes(5);  // 5分钟前的订单
        List<Order> pendingOrders = orderRepository.findPendingPaymentOrders(checkTime);
        
        for (Order order : pendingOrders) {
            try {
                // 2. 查询微信支付订单状态
                Map<String, Object> payResult = wxPayService.queryPayOrder(order.getOrderNo());
                String tradeState = (String) payResult.get("trade_state");
                
                // 3. 根据支付状态更新订单
                switch (tradeState) {
                    case "SUCCESS":
                        // 支付成功
                        order.setPaymentStatus(Order.PaymentStatus.PAID);
                        order.setPaymentTime(LocalDateTime.now());
                        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.PAID,
                                null, "系统", "支付成功", OrderStatusHistory.OperationType.PAY);
                        log.info("订单{}支付成功", order.getOrderNo());
                        break;
                        
                    case "CLOSED":
                        // 订单已关闭
                        order.setPaymentStatus(Order.PaymentStatus.CLOSED);
                        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.CANCELLED,
                                null, "系统", "支付订单已关闭", OrderStatusHistory.OperationType.PAY);
                        log.info("订单{}支付已关闭", order.getOrderNo());
                        break;
                        
                    case "PAYERROR":
                        // 支付失败
                        order.setPaymentStatus(Order.PaymentStatus.FAILED);
                        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.PAYMENT_FAILED,
                                null, "系统", "支付失败", OrderStatusHistory.OperationType.PAY);
                        log.info("订单{}支付失败", order.getOrderNo());
                        break;
                        
                    case "NOTPAY":
                        // 未支付，检查是否超时
                        if (isPaymentTimeout(order)) {
                            // 关闭支付订单
                            wxPayService.closePayOrder(order.getOrderNo());
                            // 更新订单状态
                            order.setPaymentStatus(Order.PaymentStatus.CLOSED);
                            orderService.updateOrderStatus(order.getId(), Order.OrderStatus.CANCELLED,
                                    null, "系统", "支付超时自动取消", OrderStatusHistory.OperationType.PAY);
                            log.info("订单{}支付超时自动取消", order.getOrderNo());
                        }
                        break;
                        
                    default:
                        log.warn("订单{}未知支付状态: {}", order.getOrderNo(), tradeState);
                }
            } catch (Exception e) {
                log.error("检查订单{}支付状态失败", order.getOrderNo(), e);
            }
        }
        
        log.info("未支付订单状态检查完成，共处理{}个订单", pendingOrders.size());
    }

    /**
     * 检查订单是否支付超时
     */
    private boolean isPaymentTimeout(Order order) {
        LocalDateTime timeoutTime = order.getCreatedAt().plusMinutes(30);  // 30分钟超时
        return LocalDateTime.now().isAfter(timeoutTime);
    }
} 