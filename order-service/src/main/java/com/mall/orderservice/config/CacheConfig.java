package com.mall.orderservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String PAYMENT_PASSWORD_CACHE = "payment_password";
    public static final String ORDER_CACHE = "order";
    public static final String REFUND_CACHE = "refund";
    
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    private static final Duration PAYMENT_PASSWORD_TTL = Duration.ofDays(1);
    private static final Duration ORDER_TTL = Duration.ofHours(24);
    private static final Duration REFUND_TTL = Duration.ofHours(12);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 支付密码缓存配置
        cacheConfigurations.put(PAYMENT_PASSWORD_CACHE, 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(PAYMENT_PASSWORD_TTL)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())));

        // 订单缓存配置
        cacheConfigurations.put(ORDER_CACHE,
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ORDER_TTL)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())));

        // 退款缓存配置
        cacheConfigurations.put(REFUND_CACHE,
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(REFUND_TTL)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(DEFAULT_TTL)
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
} 