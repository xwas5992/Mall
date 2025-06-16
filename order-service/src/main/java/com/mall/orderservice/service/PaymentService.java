

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentPasswordService paymentPasswordService;

    /**
     * 创建支付订单
     */
    @Transactional
    public Payment createPayment(Long userId, Long orderId, String paymentMethod, String paymentPassword) {
        // 验证支付密码
        if (!paymentPasswordService.verifyPaymentPassword(userId, paymentPassword)) {
            throw new BusinessException("支付密码验证失败");
        }

        // ... existing payment creation code ...
    }
} 