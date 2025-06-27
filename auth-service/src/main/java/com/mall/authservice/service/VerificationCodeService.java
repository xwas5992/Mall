package com.mall.authservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeService {
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateCode(String key) {
        String code = String.format("%06d", random.nextInt(1000000));
        codeStore.put(key, code);
        return code;
    }

    public boolean validateCode(String key, String code) {
        String realCode = codeStore.get(key);
        if (realCode != null && realCode.equals(code)) {
            codeStore.remove(key);
            return true;
        }
        return false;
    }
} 