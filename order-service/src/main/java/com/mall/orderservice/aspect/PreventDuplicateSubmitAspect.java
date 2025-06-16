

import com.mall.orderservice.annotation.PreventDuplicateSubmit;
import com.mall.orderservice.exception.BusinessException;
import com.mall.orderservice.util.DistributedLockUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PreventDuplicateSubmitAspect {

    private final DistributedLockUtil distributedLockUtil;

    @Around("@annotation(preventDuplicateSubmit)")
    public Object around(ProceedingJoinPoint point, PreventDuplicateSubmit preventDuplicateSubmit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        
        // 生成锁的key
        String lockKey = generateLockKey(point, request, preventDuplicateSubmit);
        
        // 尝试获取锁
        boolean locked = distributedLockUtil.tryLock(
                lockKey,
                preventDuplicateSubmit.expire(),
                preventDuplicateSubmit.expire(),
                preventDuplicateSubmit.timeUnit()
        );

        if (!locked) {
            log.warn("重复提交请求: {}", lockKey);
            throw new BusinessException(preventDuplicateSubmit.message());
        }

        try {
            // 执行目标方法
            return point.proceed();
        } finally {
            // 释放锁
            distributedLockUtil.unlock(lockKey);
        }
    }

    /**
     * 生成锁的key
     */
    private String generateLockKey(ProceedingJoinPoint point, HttpServletRequest request, 
                                 PreventDuplicateSubmit preventDuplicateSubmit) {
        StringBuilder key = new StringBuilder();
        
        // 添加前缀
        if (!preventDuplicateSubmit.prefix().isEmpty()) {
            key.append(preventDuplicateSubmit.prefix()).append(":");
        }
        
        // 添加用户标识
        String userId = request.getHeader("X-User-Id");
        if (userId != null) {
            key.append("user:").append(userId).append(":");
        }
        
        // 添加请求路径
        key.append("path:").append(request.getRequestURI()).append(":");
        
        // 添加方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        key.append("method:").append(method.getDeclaringClass().getSimpleName())
           .append(".").append(method.getName()).append(":");
        
        // 添加参数
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            String argsStr = Arrays.stream(args)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            key.append("args:").append(argsStr);
        }
        
        return key.toString();
    }
} 