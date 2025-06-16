

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockUtil {

    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_WAIT_TIME = 3;
    private static final long DEFAULT_LEASE_TIME = 5;

    /**
     * 尝试获取分布式锁
     *
     * @param key 锁的key
     * @param waitTime 等待时间
     * @param leaseTime 租约时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;
        long startTime = System.currentTimeMillis();
        long waitTimeMillis = unit.toMillis(waitTime);
        
        try {
            while (true) {
                // 尝试获取锁
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(lockKey, "1", leaseTime, unit);
                
                if (Boolean.TRUE.equals(success)) {
                    log.debug("获取分布式锁成功: {}", lockKey);
                    return true;
                }

                // 判断是否超过等待时间
                if (System.currentTimeMillis() - startTime > waitTimeMillis) {
                    log.warn("获取分布式锁超时: {}", lockKey);
                    return false;
                }

                // 短暂休眠后重试
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断: {}", lockKey, e);
            return false;
        } catch (Exception e) {
            log.error("获取分布式锁异常: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param key 锁的key
     */
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        try {
            Boolean success = redisTemplate.delete(lockKey);
            if (Boolean.TRUE.equals(success)) {
                log.debug("释放分布式锁成功: {}", lockKey);
            } else {
                log.warn("释放分布式锁失败: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("释放分布式锁异常: {}", lockKey, e);
        }
    }

    /**
     * 使用默认参数尝试获取分布式锁
     */
    public boolean tryLock(String key) {
        return tryLock(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.SECONDS);
    }
} 