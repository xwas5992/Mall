

import com.mall.orderservice.annotation.PreventDuplicateSubmit;
import com.mall.orderservice.dto.PaymentRequest;
import com.mall.orderservice.model.Payment;
import com.mall.orderservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PreventDuplicateSubmit(prefix = "payment", expire = 10, message = "支付请求正在处理中，请勿重复提交")
    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(
                userId, 
                request.getOrderId(), 
                request.getPaymentMethod(),
                request.getPaymentPassword()
        ));
    }

    /**
     * 取消支付
     */
    @PreventDuplicateSubmit(prefix = "payment_cancel", expire = 5, message = "取消支付请求正在处理中，请勿重复提交")
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancelPayment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String paymentId) {
        paymentService.cancelPayment(userId, paymentId);
        return ResponseEntity.ok().build();
    }

    /**
     * 申请退款
     */
    @PreventDuplicateSubmit(prefix = "refund", expire = 10, message = "退款申请正在处理中，请勿重复提交")
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Void> refundPayment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String paymentId,
            @RequestParam String reason) {
        paymentService.refundPayment(userId, paymentId, reason);
        return ResponseEntity.ok().build();
    }
} 