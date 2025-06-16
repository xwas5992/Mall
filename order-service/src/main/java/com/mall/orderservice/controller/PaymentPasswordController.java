

import com.mall.orderservice.dto.PaymentPasswordRequest;
import com.mall.orderservice.service.PaymentPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment-password")
@RequiredArgsConstructor
public class PaymentPasswordController {

    private final PaymentPasswordService paymentPasswordService;

    /**
     * 设置支付密码
     */
    @PostMapping("/set")
    public ResponseEntity<Void> setPaymentPassword(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PaymentPasswordRequest request) {
        paymentPasswordService.setPaymentPassword(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 验证支付密码
     */
    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyPaymentPassword(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String password) {
        boolean isValid = paymentPasswordService.verifyPaymentPassword(userId, password);
        return ResponseEntity.ok(isValid);
    }

    /**
     * 重置支付密码
     */
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPaymentPassword(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PaymentPasswordRequest request) {
        paymentPasswordService.resetPaymentPassword(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 检查是否已设置支付密码
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkPaymentPassword(@RequestHeader("X-User-Id") Long userId) {
        boolean hasPassword = paymentPasswordService.hasPaymentPassword(userId);
        return ResponseEntity.ok(hasPassword);
    }
} 