

import lombok.Getter;

@Getter
public class OrderException extends BusinessException {
    private final String orderNo;

    public OrderException(String code, String message) {
        super(code, message);
        this.orderNo = null;
    }

    public OrderException(String code, String message, String orderNo) {
        super(code, message);
        this.orderNo = orderNo;
    }

    public OrderException(String code, String message, Throwable cause) {
        super(code, message, cause);
        this.orderNo = null;
    }

    public OrderException(String code, String message, String orderNo, Throwable cause) {
        super(code, message, cause);
        this.orderNo = orderNo;
    }

    // 预定义的订单异常
    public static OrderException orderNotFound(String orderNo) {
        return new OrderException("ORDER_NOT_FOUND", "订单不存在: " + orderNo, orderNo);
    }

    public static OrderException invalidOrderStatus(String orderNo, String currentStatus, String expectedStatus) {
        return new OrderException("INVALID_ORDER_STATUS", 
            String.format("订单状态错误: %s, 当前状态: %s, 期望状态: %s", orderNo, currentStatus, expectedStatus),
            orderNo);
    }

    public static OrderException orderClosed(String orderNo) {
        return new OrderException("ORDER_CLOSED", "订单已关闭: " + orderNo, orderNo);
    }

    public static OrderException orderCancelled(String orderNo) {
        return new OrderException("ORDER_CANCELLED", "订单已取消: " + orderNo, orderNo);
    }

    public static OrderException orderRefunded(String orderNo) {
        return new OrderException("ORDER_REFUNDED", "订单已退款: " + orderNo, orderNo);
    }

    public static OrderException invalidOrderAmount(String orderNo, String reason) {
        return new OrderException("INVALID_ORDER_AMOUNT", "订单金额错误: " + reason, orderNo);
    }

    public static OrderException duplicateOrder(String orderNo) {
        return new OrderException("DUPLICATE_ORDER", "重复订单: " + orderNo, orderNo);
    }

    public static OrderException orderTimeout(String orderNo) {
        return new OrderException("ORDER_TIMEOUT", "订单超时: " + orderNo, orderNo);
    }

    public static OrderException invalidOrderItems(String orderNo, String reason) {
        return new OrderException("INVALID_ORDER_ITEMS", "订单商品错误: " + reason, orderNo);
    }

    public static OrderException insufficientStock(String orderNo, String productName) {
        return new OrderException("INSUFFICIENT_STOCK", 
            String.format("商品库存不足: %s, 订单号: %s", productName, orderNo), orderNo);
    }

    public static OrderException invalidDeliveryInfo(String orderNo, String reason) {
        return new OrderException("INVALID_DELIVERY_INFO", "配送信息错误: " + reason, orderNo);
    }

    public static OrderException invalidUserInfo(String orderNo, String reason) {
        return new OrderException("INVALID_USER_INFO", "用户信息错误: " + reason, orderNo);
    }
} 