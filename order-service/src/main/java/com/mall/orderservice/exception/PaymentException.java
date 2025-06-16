

import lombok.Getter;

@Getter
public class PaymentException extends BusinessException {
    private final Object details;

    public PaymentException(String code, String message) {
        super(code, message);
        this.details = null;
    }

    public PaymentException(String code, String message, Object details) {
        super(code, message);
        this.details = details;
    }

    public PaymentException(String code, String message, Throwable cause) {
        super(code, message, cause);
        this.details = null;
    }

    public PaymentException(String code, String message, Object details, Throwable cause) {
        super(code, message, cause);
        this.details = details;
    }

    // 预定义的支付异常
    public static PaymentException orderNotFound(String orderNo) {
        return new PaymentException("ORDER_NOT_FOUND", "订单不存在: " + orderNo);
    }

    public static PaymentException invalidOrderStatus(String orderNo, String currentStatus, String expectedStatus) {
        return new PaymentException("INVALID_ORDER_STATUS", 
            String.format("订单状态错误: %s, 当前状态: %s, 期望状态: %s", orderNo, currentStatus, expectedStatus));
    }

    public static PaymentException paymentFailed(String orderNo, String reason) {
        return new PaymentException("PAYMENT_FAILED", "支付失败: " + reason, orderNo);
    }

    public static PaymentException refundFailed(String orderNo, String reason) {
        return new PaymentException("REFUND_FAILED", "退款失败: " + reason, orderNo);
    }

    public static PaymentException invalidAmount(String orderNo, String reason) {
        return new PaymentException("INVALID_AMOUNT", "金额错误: " + reason, orderNo);
    }

    public static PaymentException paymentTimeout(String orderNo) {
        return new PaymentException("PAYMENT_TIMEOUT", "支付超时: " + orderNo);
    }

    public static PaymentException duplicatePayment(String orderNo) {
        return new PaymentException("DUPLICATE_PAYMENT", "重复支付: " + orderNo);
    }

    public static PaymentException invalidSignature() {
        return new PaymentException("INVALID_SIGNATURE", "签名验证失败");
    }

    public static PaymentException decryptFailed() {
        return new PaymentException("DECRYPT_FAILED", "数据解密失败");
    }
} 