

import com.mall.orderservice.config.CacheConfig;
import com.mall.orderservice.dto.PaymentPasswordRequest;
import com.mall.orderservice.exception.BusinessException;
import com.mall.orderservice.model.PaymentPassword;
import com.mall.orderservice.repository.PaymentPasswordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentPasswordService {

    private final PaymentPasswordRepository paymentPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    private static final Random RANDOM = new Random();

    /**
     * 设置支付密码
     */
    @Transactional
    @CacheEvict(value = CacheConfig.PAYMENT_PASSWORD_CACHE, key = "#userId")
    public void setPaymentPassword(Long userId, PaymentPasswordRequest request) {
        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查是否已设置过支付密码
        if (paymentPasswordRepository.existsByUserId(userId)) {
            throw new BusinessException("已设置过支付密码，如需修改请使用重置密码功能");
        }

        // 生成盐值
        String salt = generateSalt();
        // 加密密码
        String passwordHash = passwordEncoder.encode(request.getPassword() + salt);

        // 创建支付密码记录
        PaymentPassword paymentPassword = new PaymentPassword();
        paymentPassword.setUserId(userId);
        paymentPassword.setPasswordHash(passwordHash);
        paymentPassword.setSalt(salt);

        paymentPasswordRepository.save(paymentPassword);
        log.info("用户 {} 设置支付密码成功", userId);
    }

    /**
     * 验证支付密码
     */
    @Transactional
    @Cacheable(value = CacheConfig.PAYMENT_PASSWORD_CACHE, key = "#userId", unless = "#result == false")
    public boolean verifyPaymentPassword(Long userId, String password) {
        PaymentPassword paymentPassword = paymentPasswordRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("未设置支付密码"));

        // 检查密码是否被锁定
        if (paymentPassword.getLockedUntil() != null && 
            paymentPassword.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException("支付密码已被锁定，请稍后再试");
        }

        // 验证密码
        boolean isValid = passwordEncoder.matches(password + paymentPassword.getSalt(), 
                                                paymentPassword.getPasswordHash());

        if (isValid) {
            // 密码正确，重置失败次数
            if (paymentPassword.getFailedAttempts() > 0) {
                paymentPassword.setFailedAttempts(0);
                paymentPassword.setLastFailedTime(null);
                paymentPassword.setLockedUntil(null);
                paymentPasswordRepository.save(paymentPassword);
            }
            return true;
        } else {
            // 密码错误，增加失败次数
            int failedAttempts = paymentPassword.getFailedAttempts() + 1;
            paymentPassword.setFailedAttempts(failedAttempts);
            paymentPassword.setLastFailedTime(LocalDateTime.now());

            // 检查是否需要锁定
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                paymentPassword.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                log.warn("用户 {} 支付密码连续失败 {} 次，已锁定 {} 分钟", 
                        userId, failedAttempts, LOCK_DURATION_MINUTES);
            }

            paymentPasswordRepository.save(paymentPassword);
            // 验证失败时清除缓存
            evictPaymentPasswordCache(userId);
            throw new BusinessException("支付密码错误，还剩" + (MAX_FAILED_ATTEMPTS - failedAttempts) + "次机会");
        }
    }

    /**
     * 重置支付密码
     */
    @Transactional
    @CacheEvict(value = CacheConfig.PAYMENT_PASSWORD_CACHE, key = "#userId")
    public void resetPaymentPassword(Long userId, PaymentPasswordRequest request) {
        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        PaymentPassword paymentPassword = paymentPasswordRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("未设置支付密码"));

        // 生成新的盐值
        String salt = generateSalt();
        // 加密新密码
        String passwordHash = passwordEncoder.encode(request.getPassword() + salt);

        // 更新支付密码
        paymentPassword.setPasswordHash(passwordHash);
        paymentPassword.setSalt(salt);
        paymentPassword.setFailedAttempts(0);
        paymentPassword.setLastFailedTime(null);
        paymentPassword.setLockedUntil(null);

        paymentPasswordRepository.save(paymentPassword);
        log.info("用户 {} 重置支付密码成功", userId);
    }

    /**
     * 检查是否已设置支付密码
     */
    @Cacheable(value = CacheConfig.PAYMENT_PASSWORD_CACHE, key = "'exists:' + #userId")
    public boolean hasPaymentPassword(Long userId) {
        return paymentPasswordRepository.existsByUserId(userId);
    }

    /**
     * 清除支付密码缓存
     */
    @CacheEvict(value = CacheConfig.PAYMENT_PASSWORD_CACHE, key = "#userId")
    public void evictPaymentPasswordCache(Long userId) {
        log.debug("清除用户 {} 的支付密码缓存", userId);
    }

    /**
     * 生成随机盐值
     */
    private String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
} 