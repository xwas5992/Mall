

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

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))  // 默认过期时间30分钟
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();  // 不缓存null值

        // 自定义缓存配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        // 商品详情缓存配置 - 1小时
        configMap.put("product", defaultConfig.entryTtl(Duration.ofHours(1)));
        // 商品统计缓存配置 - 5分钟
        configMap.put("productStatistics", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        // 商品评价缓存配置 - 10分钟
        configMap.put("productReview", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        // 热门商品缓存配置 - 15分钟
        configMap.put("hotProducts", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        // 搜索建议缓存配置 - 1小时
        configMap.put("searchSuggestions", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configMap)
                .build();
    }
} 