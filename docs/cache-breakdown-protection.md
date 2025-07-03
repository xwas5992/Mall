# 缓存击穿防护实现方案

## 概述

缓存击穿是指热点数据的缓存过期，导致大量并发请求直接打到数据库，造成数据库压力过大的问题。本项目使用互斥锁机制来防止缓存击穿。

## 防护机制

### 1. 互斥锁机制

**位置**: `MutexLockService.java`

**实现原理**:
- 使用Redis分布式锁确保只有一个线程可以重建缓存
- 其他线程等待锁释放后从缓存获取数据
- 防止多个线程同时查询数据库

**核心流程**:
```
1. 检查缓存是否存在
   ↓
2. 缓存不存在，尝试获取互斥锁
   ↓
3. 获取锁成功 → 查询数据库 → 设置缓存 → 释放锁
   ↓
4. 获取锁失败 → 等待100ms → 重试获取缓存
```

### 2. 分布式锁实现

**锁的获取**:
```java
// 使用SET NX EX命令原子性地设置锁
Boolean result = redisTemplate.opsForValue()
        .setIfAbsent(lockKey, lockValue, 30, TimeUnit.SECONDS);
```

**锁的释放**:
```java
// 检查锁值匹配后删除
Object currentValue = redisTemplate.opsForValue().get(lockKey);
if (lockValue.equals(currentValue)) {
    redisTemplate.delete(lockKey);
}
```

### 3. 双重检查机制

**防止重复重建**:
- 获取锁后再次检查缓存
- 防止在获取锁期间其他线程已经更新了缓存
- 提高系统效率

## 配置说明

### 锁配置参数

**默认锁TTL**: 30秒
**等待时间**: 100毫秒
**锁值格式**: `线程ID:时间戳`

### Redis配置

```properties
# Redis连接配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0
```

## 使用示例

### 1. 基本使用

```java
// 使用互斥锁获取用户数据
User user = mutexLockService.getCacheWithMutexLock(
    "user:1",           // 缓存键
    "lock:user:1",      // 锁键
    () -> userRepository.findById(1L).orElse(null)  // 数据加载器
);
```

### 2. 在UserService中的集成

```java
public User findById(Long id) {
    // 布隆过滤器检查
    if (!bloomFilterService.mightContain(id)) {
        return null;
    }
    
    // 使用互斥锁防止缓存击穿
    String cacheKey = "user:" + id;
    String lockKey = "lock:user:" + id;
    
    return mutexLockService.getCacheWithMutexLock(cacheKey, lockKey, () -> {
        return userRepository.findById(id).orElse(null);
    });
}
```

## API接口

### 1. 锁状态检查

```bash
# 检查指定锁的状态
GET /api/cache/lock/status/{lockKey}

# 响应示例
{
    "lockKey": "lock:user:9999",
    "isLocked": true,
    "ttl": 25,
    "timestamp": 1640995200000
}
```

### 2. 强制释放锁

```bash
# 强制释放指定的锁（谨慎使用）
DELETE /api/cache/lock/{lockKey}

# 响应示例
{
    "lockKey": "lock:user:9999",
    "message": "锁已强制释放",
    "timestamp": 1640995200000
}
```

### 3. 锁统计信息

```bash
# 获取所有锁的统计信息
GET /api/cache/lock/stats

# 响应示例
{
    "lockStats": {
        "lock:user:1": {
            "isLocked": false,
            "ttl": -1
        },
        "lock:user:9999": {
            "isLocked": true,
            "ttl": 20
        }
    },
    "timestamp": 1640995200000
}
```

### 4. 缓存击穿测试

```bash
# 测试缓存击穿防护
POST /api/cache/test/breakdown?userId=9999

# 响应示例
{
    "userId": 9999,
    "lockKey": "lock:user:9999",
    "isLocked": false,
    "ttl": -1,
    "message": "缓存击穿防护测试完成",
    "timestamp": 1640995200000
}
```

## 测试方法

### 1. 启动服务

```bash
# 启动Redis
D:\softinstall\Redis\redis-server.exe

# 启动用户服务
cd user-service
mvn spring-boot:run
```

### 2. 运行测试脚本

```bash
# 运行缓存击穿防护测试
test-cache-breakdown.bat
```

### 3. 手动测试

```bash
# 测试缓存击穿防护
curl http://localhost:8085/api/user/9999

# 检查锁状态
curl http://localhost:8085/api/cache/lock/status/lock:user:9999

# 模拟并发请求
# 在多个终端同时运行
curl http://localhost:8085/api/user/9999
```

### 4. 检查Redis状态

```bash
# 查看所有锁
D:\softinstall\Redis\redis-cli.exe keys "lock:*"

# 查看锁的TTL
D:\softinstall\Redis\redis-cli.exe ttl "lock:user:9999"

# 查看锁的值
D:\softinstall\Redis\redis-cli.exe get "lock:user:9999"
```

## 性能优化

### 1. 锁的优化

- **锁的粒度**: 按业务ID设置锁，避免全局锁
- **锁的TTL**: 设置合理的过期时间，防止死锁
- **锁的值**: 使用唯一标识，确保锁的安全性

### 2. 等待策略

- **等待时间**: 设置合理的等待时间，避免长时间阻塞
- **重试机制**: 失败后重试获取缓存
- **超时处理**: 设置最大等待时间

### 3. 监控指标

- **锁获取成功率**: 监控锁的竞争情况
- **等待时间**: 监控线程等待时间
- **缓存命中率**: 监控缓存效果

## 故障排查

### 1. 常见问题

**问题**: 锁无法获取
**解决**: 检查Redis连接和锁的TTL设置

**问题**: 死锁
**解决**: 检查锁的释放逻辑和TTL设置

**问题**: 性能下降
**解决**: 优化锁的粒度和等待策略

### 2. 日志分析

查看应用日志中的关键信息：
- 锁获取和释放日志
- 等待重试日志
- 缓存重建日志

### 3. 监控告警

建议设置以下监控指标：
- Redis连接状态
- 锁的竞争情况
- 响应时间异常
- 数据库连接数

## 扩展建议

### 1. 分布式锁优化

- 使用Redisson等成熟的分布式锁库
- 支持可重入锁和公平锁
- 提供更丰富的锁特性

### 2. 锁的降级策略

- 锁获取失败时的降级处理
- 使用本地缓存作为备选方案
- 设置熔断机制

### 3. 智能锁管理

- 动态调整锁的TTL
- 基于业务负载调整锁策略
- 锁的自动清理机制

### 4. 监控和告警

- 实时监控锁的状态
- 设置锁超时告警
- 锁竞争情况分析 