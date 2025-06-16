

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.mall.productservice.service.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.mall.productservice.controller.*.*(..))")
    public void controllerLayer() {}

    @Around("serviceLayer() || controllerLayer()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("开始执行 {}.{} 方法，参数: {}", className, methodName, args);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            log.info("完成执行 {}.{} 方法，耗时: {}ms，返回: {}", 
                    className, methodName, stopWatch.getTotalTimeMillis(), result);
            return result;
        } catch (Exception e) {
            log.error("执行 {}.{} 方法时发生错误: {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }
} 