# 缓存穿透防护实现方案

## 概述

本项目实现了完整的缓存穿透防护机制，包括参数校验拦截、布隆过滤器和空值缓存，有效防止恶意请求对数据库造成压力。

## 防护机制

### 1. 参数校验拦截器

**位置**: `user-service/src/main/java/com/mall/userservice/interceptor/ParameterValidationInterceptor.java`

**功能**:
- 拦截无效的ID参数（负数、零、超大数值）
- 在请求到达业务逻辑前直接返回错误响应
- 防止无效请求消耗系统资源

**拦截规则**:
- ID <= 0: 拦截
- ID > 999999999: 拦截（防止溢出攻击）
- 非数字ID: 拦截

### 2. 布隆过滤器

**位置**: `user-service/src/main/java/com/mall/userservice/service/BloomFilterService.java`

**功能**:
- 快速判断用户ID是否可能存在
- 在应用启动时从数据库加载所有用户ID
- 误判率设置为0.01，预期元素数量10000

**优势**:
- 内存占用小
- 查询速度快
- 可以有效过滤大部分不存在的ID

### 3. 空值缓存

**位置**: `user-service/src/main/java/com/mall/userservice/service/CacheService.java`

**功能**:
- 对不存在的用户ID缓存空值
- 空值缓存过期时间较短（2分钟）
- 正常数据缓存过期时间较长（30分钟）

**实现**:
```java
// 空值缓存
redisTemplate.opsForValue().set(key, "NULL_VALUE", 2, TimeUnit.MINUTES);

// 正常数据缓存
redisTemplate.opsForValue().set(key, userData, 30, TimeUnit.MINUTES);
```

## 请求处理流程

```
1. 请求到达
   ↓
2. 参数校验拦截器
   ├─ 无效ID → 返回400错误
   └─ 有效ID → 继续
   ↓
3. 布隆过滤器检查
   ├─ 不存在 → 直接返回null
   └─ 可能存在 → 继续
   ↓
4. Redis缓存检查
   ├─ 命中 → 返回缓存数据
   └─ 未命中 → 继续
   ↓
5. 数据库查询
   ├─ 存在 → 缓存数据，添加到布隆过滤器
   └─ 不存在 → 缓存空值
   ↓
6. 返回结果
```

## 配置说明

### 缓存配置

**文件**: `user-service/src/main/resources/application.properties`

```properties
# Redis配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0

# 缓存配置
spring.cache.type=redis
spring.cache.redis.time-to-live=1800000
spring.cache.redis.cache-null-values=false
spring.cache.redis.use-key-prefix=true
spring.cache.redis.key-prefix=user:
```

### 布隆过滤器配置

**预期元素数量**: 10000
**误判率**: 0.01
**内存占用**: 约12KB

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
# 运行缓存穿透防护测试
test-cache-penetration.bat
```

### 3. 手动测试

```bash
# 测试无效ID拦截
curl http://localhost:8085/api/user/-3872
curl http://localhost:8085/api/user/0
curl http://localhost:8085/api/user/999999999999

# 测试正常ID
curl http://localhost:8085/api/user/1

# 测试不存在的ID
curl http://localhost:8085/api/user/9999
```

### 4. 检查Redis缓存

```bash
# 查看所有用户缓存
D:\softinstall\Redis\redis-cli.exe keys "user:*"

# 查看特定缓存
D:\softinstall\Redis\redis-cli.exe get "user:9999"
```

## 性能优化

### 1. 缓存策略

- **热点数据**: 30分钟过期
- **空值数据**: 2分钟过期
- **会话数据**: 按需设置

### 2. 布隆过滤器优化

- 定期重新加载布隆过滤器
- 监控误判率
- 根据实际数据量调整参数

### 3. 监控指标

- 缓存命中率
- 布隆过滤器过滤率
- 数据库查询次数
- 响应时间

## 注意事项

### 1. 内存管理

- 空值缓存会占用Redis内存
- 需要监控Redis内存使用情况
- 考虑设置Redis内存上限

### 2. 数据一致性

- 用户数据更新时需要清除缓存
- 布隆过滤器需要定期更新
- 考虑使用消息队列同步数据

### 3. 扩展性

- 可以扩展到其他服务
- 支持分布式部署
- 考虑使用Redis Cluster

## 故障排查

### 1. 常见问题

**问题**: 布隆过滤器初始化失败
**解决**: 检查数据库连接和用户表数据

**问题**: Redis连接失败
**解决**: 检查Redis服务状态和配置

**问题**: 缓存穿透仍然发生
**解决**: 检查布隆过滤器参数和缓存配置

### 2. 日志分析

查看应用日志中的关键信息：
- 布隆过滤器初始化日志
- 缓存命中/未命中日志
- 参数校验拦截日志

### 3. 监控告警

建议设置以下监控指标：
- Redis内存使用率
- 缓存命中率
- 数据库连接数
- 响应时间异常 