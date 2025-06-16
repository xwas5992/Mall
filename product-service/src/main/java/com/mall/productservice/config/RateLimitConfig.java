

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health", "/api/metrics");
    }

    public class RateLimitInterceptor extends HandlerInterceptorAdapter {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String clientId = getClientId(request);
            Bucket bucket = buckets.computeIfAbsent(clientId, this::createNewBucket);

            if (bucket.tryConsume(1)) {
                return true;
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("请求过于频繁，请稍后再试");
                return false;
            }
        }

        private Bucket createNewBucket(String clientId) {
            // 根据不同的客户端类型设置不同的限流策略
            if (clientId.startsWith("admin")) {
                // 管理员：每分钟100次请求
                return createBucket(100, Duration.ofMinutes(1));
            } else if (clientId.startsWith("api")) {
                // API调用：每分钟50次请求
                return createBucket(50, Duration.ofMinutes(1));
            } else {
                // 普通用户：每分钟20次请求
                return createBucket(20, Duration.ofMinutes(1));
            }
        }

        private Bucket createBucket(int tokens, Duration duration) {
            Refill refill = Refill.intervally(tokens, duration);
            Bandwidth limit = Bandwidth.classic(tokens, refill);
            return Bucket4j.builder().addLimit(limit).build();
        }

        private String getClientId(HttpServletRequest request) {
            String clientId = request.getHeader("X-Client-Id");
            if (clientId == null) {
                // 如果没有客户端ID，使用IP地址
                clientId = request.getRemoteAddr();
            }
            return clientId;
        }
    }
} 