package com.mall.userservice.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数校验拦截器
 * 拦截无效的ID参数，防止缓存穿透
 */
@Slf4j
@Component
public class ParameterValidationInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        // 排除地址相关的API，避免误拦截
        if (path.contains("/address/")) {
            return true;
        }
        
        // 检查是否包含ID参数
        if (path.contains("/{id}") || path.matches(".*/\\d+.*")) {
            String idParam = extractIdFromPath(path);
            
            if (idParam != null && !isValidId(idParam)) {
                log.warn("拦截无效ID请求: {} - ID: {}", path, idParam);
                
                // 返回错误响应
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "无效的ID参数");
                errorResponse.put("code", "INVALID_ID");
                errorResponse.put("timestamp", System.currentTimeMillis());
                
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 从路径中提取ID参数
     */
    private String extractIdFromPath(String path) {
        // 匹配路径中的数字ID
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.matches("-?\\d+")) {
                return part;
            }
        }
        return null;
    }
    
    /**
     * 验证ID是否有效
     */
    private boolean isValidId(String idStr) {
        try {
            long id = Long.parseLong(idStr);
            
            // 拦截明显无效的ID
            if (id <= 0) {
                return false;
            }
            
            // 拦截过大的ID（防止溢出攻击）
            if (id > 999999999L) {
                return false;
            }
            
            // 拦截负数ID
            if (id < 0) {
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 