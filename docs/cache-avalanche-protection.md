# 缓存雪崩防护实现方案

## 概述

缓存雪崩是指大量缓存同时过期，导致大量请求直接打到数据库，造成数据库压力过大的问题。本项目实现了多种防护机制来防止缓存雪崩。

## 防护机制

### 1. 随机TTL（过期时间分散）

**位置**: `CacheAvalancheProtectionService.java`

**实现原理**:
- 在基础TTL基础上添加随机偏移（±10%）
- 确保缓存不会同时过期
- 防止集体失效导致的雪崩

**代码实现**:
```java
// 基础TTL + 随机时间（±10%）
long randomOffset = (long) (baseTTL * 0.1 * (random.nextDouble() - 0.5));
long finalTTL = Math.max(baseTTL + randomOffset, baseTTL * 0.9);
```

### 2. 多级缓存

**位置**: `HighAvailabilityCacheService.java`

**实现原理**:
- 主Redis + 备用Redis
- 故障自动转移
- 提高缓存服务可用性

**架构**:
```
应用层
  ├─ 主Redis (6379端口)
  └─ 备用Redis (6380端口)
```

### 3. 缓存高可用集群

**位置**: `RedisClusterConfig.java`

**功能**:
- 配置主备Redis连接
- 自动故障检测
- 连接池管理

### 4. 锁机制防护

**位置**: `CacheAvalancheProtectionService.java`

**实现原理**:
- 使用ReentrantLock防止缓存击穿
- 只有一个线程可以重建缓存
- 其他线程等待或重试

**代码实现**:
```java
if (cacheLock.tryLock()) {
    try {
        // 重建缓存逻辑
    } finally {
        cacheLock.unlock();
    }
}
```

### 5. 缓存标记

**位置**: `CacheAvalancheProtectionService.java`

**实现原理**:
- 热点数据设置永不过期标记
- 后台异步更新缓存
- 适用于不严格要求一致性的场景

**代码实现**:
```java
// 设置热点数据缓存（永不过期）
redisTemplate.opsForValue().set(key, value);
redisTemplate.opsForValue().set(key + ":marker", CACHE_MARKER);
```

### 6. 缓存预热

**位置**: `CacheWarmUpService.java`

**实现原理**:
- 系统启动时预先加载热点数据
- 避免冷启动导致的缓存雪崩
- 自动识别热点用户

## 配置说明

### Redis集群配置

**主Redis配置**:
```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0
```

**备用Redis配置**:
```properties
# 备用Redis (6380端口)
backup.redis.host=localhost
backup.redis.port=6380
backup.redis.database=0
```

### 缓存策略配置

**热点数据**:
- 永不过期 + 后台更新
- 适用于用户ID 1-10的用户

**普通数据**:
- 30分钟基础TTL + 随机偏移
- 空值缓存2分钟

**系统配置**:
- 60-120分钟TTL
- 统计信息、版本号等

## 使用示例

### 1. 基本缓存操作

```java
// 设置缓存（带随机TTL）
avalancheProtectionService.setCacheWithRandomTTL("user:1", user, 30, TimeUnit.MINUTES);

// 获取缓存（带锁保护）
User user = (User) avalancheProtectionService.getCacheWithLock("user:1", () -> {
    return userRepository.findById(1L).orElse(null);
});
```

### 2. 热点数据缓存

```java
// 设置热点数据（永不过期）
avalancheProtectionService.setHotDataCache("user:1", user);

// 检查是否为热点数据
boolean isHot = avalancheProtectionService.isHotData("user:1");
```

### 3. 高可用缓存

```java
// 设置缓存（自动故障转移）
haCacheService.setCache("user:1", user, 30, TimeUnit.MINUTES);

// 获取缓存（自动故障转移）
Object cached = haCacheService.getCache("user:1");
```

## 测试方法

### 1. 启动服务

```bash
# 启动主Redis
D:\softinstall\Redis\redis-server.exe

# 启动备用Redis（可选）
D:\softinstall\Redis\redis-server.exe redis.conf --port 6380

# 启动用户服务
cd user-service
mvn spring-boot:run
```

### 2. 运行测试脚本

```bash
# 运行缓存雪崩防护测试
test-cache-avalanche.bat
```

### 3. 手动测试

```bash
# 测试缓存预热
curl http://localhost:8085/api/user/1

# 测试热点数据
curl http://localhost:8085/api/user/1

# 测试并发请求（模拟缓存击穿）
# 在多个终端同时运行
curl http://localhost:8085/api/user/9999
```

### 4. 检查Redis状态

```bash
# 查看所有缓存
D:\softinstall\Redis\redis-cli.exe keys "*"

# 查看热点数据标记
D:\softinstall\Redis\redis-cli.exe keys "*:marker"

# 查看缓存TTL
D:\softinstall\Redis\redis-cli.exe ttl "user:1"
```

## 性能优化

### 1. 缓存策略优化

- **热点数据**: 永不过期 + 后台更新
- **普通数据**: 随机TTL + 分级缓存
- **冷数据**: 较短TTL + 按需加载

### 2. 并发控制

- **锁机制**: 防止缓存击穿
- **队列机制**: 控制并发重建
- **重试机制**: 提高成功率

### 3. 监控指标

- **缓存命中率**: 监控缓存效果
- **响应时间**: 监控性能
- **错误率**: 监控稳定性
- **内存使用**: 监控资源

## 故障排查

### 1. 常见问题

**问题**: 缓存预热失败
**解决**: 检查数据库连接和用户数据

**问题**: Redis连接失败
**解决**: 检查Redis服务状态和网络连接

**问题**: 缓存雪崩仍然发生
**解决**: 检查TTL配置和随机化逻辑

### 2. 日志分析

查看应用日志中的关键信息：
- 缓存预热日志
- 锁获取日志
- 故障转移日志
- 热点数据标记日志

### 3. 监控告警

建议设置以下监控指标：
- Redis连接状态
- 缓存命中率
- 响应时间异常
- 锁等待时间

## 扩展建议

### 1. 分布式锁

- 使用Redis分布式锁
- 支持多实例部署
- 提高并发性能

### 2. 缓存更新策略

- 定时更新热点数据
- 事件驱动更新
- 版本号控制

### 3. 缓存分级

- L1: 本地缓存（Caffeine）
- L2: Redis缓存
- L3: 数据库

### 4. 智能预热

- 基于访问频率预热
- 机器学习预测热点
- 动态调整策略 