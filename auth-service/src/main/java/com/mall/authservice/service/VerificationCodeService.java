package com.mall.authservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 手机号验证码服务
 */
@Service
public class VerificationCodeService {
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /**
     * 生成验证码
     * @param phone 手机号
     * @return 6位数字验证码
     */
    public String generateCode(String phone) {
        String code = String.format("%06d", random.nextInt(1000000));
        codeStore.put(phone, code);
        return code;
    }

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 验证结果
     */
    public boolean validateCode(String phone, String code) {
        String realCode = codeStore.get(phone);
        if (realCode != null && realCode.equals(code)) {
            codeStore.remove(phone);
            return true;
        }
        return false;
    }
} 