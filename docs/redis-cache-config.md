# Redis 缓存配置文档

## 概述

本项目已配置Redis作为缓存中间件，用于提升系统性能和用户体验。Redis缓存被用于以下场景：

- 用户认证和会话管理
- 商品信息缓存
- 购物车数据缓存
- 搜索结果缓存
- 用户资料缓存

## 本地Redis配置

### Redis安装路径
- **本地Redis路径**: `D:\softinstall\Redis`
- **Redis服务器**: `D:\softinstall\Redis\redis-server.exe`
- **Redis客户端**: `D:\softinstall\Redis\redis-cli.exe`

## 服务配置

### 1. 认证服务 (auth-service)
- **数据库**: Redis DB 1
- **主要缓存**:
  - 用户信息缓存 (60分钟)
  - Token黑名单 (15分钟)
  - 验证码缓存 (5分钟)
  - 登录失败次数缓存 (30分钟)
  - 用户会话缓存 (30分钟)

### 2. 商品服务 (product-service)
- **数据库**: Redis DB 0
- **主要缓存**:
  - 商品信息缓存 (1小时)
  - 分类信息缓存 (2小时)
  - 首页商品缓存 (15分钟)
  - 搜索结果缓存 (30分钟)
  - 购物车缓存 (10分钟)
  - 热门商品缓存 (15分钟)

### 3. 用户服务 (user-service)
- **数据库**: Redis DB 2
- **主要缓存**:
  - 用户信息缓存 (60分钟)
  - 用户资料缓存 (30分钟)
  - 用户会话缓存 (30分钟)
  - 用户偏好设置缓存 (60分钟)

## 手动启动本地Redis

### 1. 检查Redis安装

首先确认您的Redis安装路径是否正确：

```bash
# 检查Redis服务器是否存在
dir D:\softinstall\Redis\redis-server.exe

# 检查Redis客户端是否存在
dir D:\softinstall\Redis\redis-cli.exe
```

### 2. 启动Redis服务器

#### 方法一：直接启动
```bash
# 进入Redis目录
cd D:\softinstall\Redis

# 启动Redis服务器
redis-server.exe

# 或者指定配置文件启动
redis-server.exe redis.windows.conf
```

#### 方法二：后台启动
```bash
# 后台启动Redis服务器
start "Redis Server" D:\softinstall\Redis\redis-server.exe
```

#### 方法三：使用项目脚本
```bash
# 使用项目提供的启动脚本
./start-redis.bat
```

### 3. 验证Redis启动

```bash
# 测试Redis连接
D:\softinstall\Redis\redis-cli.exe ping

# 应该返回: PONG
```

### 4. 基本Redis操作测试

```bash
# 进入Redis客户端
D:\softinstall\Redis\redis-cli.exe

# 测试基本操作
set test "hello"
get test
del test

# 查看所有key
keys *

# 退出客户端
exit
```

### 5. 检查Redis状态

```bash
# 查看Redis信息
D:\softinstall\Redis\redis-cli.exe info

# 查看内存使用
D:\softinstall\Redis\redis-cli.exe info memory

# 查看数据库信息
D:\softinstall\Redis\redis-cli.exe info keyspace
```

## 项目适配配置

### 1. 环境变量配置

为了适配您的本地Redis，建议设置环境变量：

```bash
# 设置Redis路径环境变量
set REDIS_HOME=D:\softinstall\Redis
set PATH=%REDIS_HOME%;%PATH%
```

### 2. 项目配置文件

所有服务的Redis配置已经适配本地Redis：

```properties
# Redis连接配置 (适用于所有服务)
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.timeout=10000

# 连接池配置
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-wait=-1
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=0

# 缓存配置
spring.cache.type=redis
spring.cache.redis.time-to-live=1800000  # 30分钟
spring.cache.redis.cache-null-values=false
spring.cache.redis.use-key-prefix=true
```

### 3. 不同服务的数据库配置

```properties
# 认证服务使用DB 1
spring.redis.database=1
spring.cache.redis.key-prefix=auth:

# 商品服务使用DB 0
spring.redis.database=0
spring.cache.redis.key-prefix=product:

# 用户服务使用DB 2
spring.redis.database=2
spring.cache.redis.key-prefix=user:
```

## 启动步骤

### 1. 启动Redis服务器
```bash
# 方法1: 使用项目脚本
./start-redis.bat

# 方法2: 手动启动
cd D:\softinstall\Redis
redis-server.exe
```

### 2. 验证Redis连接
```bash
D:\softinstall\Redis\redis-cli.exe ping
```

### 3. 启动项目服务
```bash
# 启动认证服务
cd auth-service
mvn spring-boot:run

# 启动商品服务
cd product-service
mvn spring-boot:run

# 启动用户服务
cd user-service
mvn spring-boot:run
```

### 4. 测试缓存功能
```bash
# 测试Redis缓存
D:\softinstall\Redis\redis-cli.exe

# 查看各服务的缓存数据
keys auth:*
keys product:*
keys user:*
```

## 缓存策略

1. **商品缓存**: 1小时过期，适合相对稳定的商品信息
2. **用户缓存**: 60分钟过期，平衡性能和数据一致性
3. **会话缓存**: 30分钟过期，适合用户会话管理
4. **搜索结果缓存**: 30分钟过期，适合搜索热点数据
5. **购物车缓存**: 10分钟过期，保证数据实时性

## 使用示例

### 1. 在服务中使用缓存

```java
@Service
public class ProductService {
    
    @Autowired
    private CacheService cacheService;
    
    // 使用@Cacheable注解
    @Cacheable(value = "product", key = "#productId")
    public Product getProduct(Long productId) {
        return productRepository.findById(productId);
    }
    
    // 使用CacheService
    public List<Product> getProductList(String category) {
        // 先尝试从缓存获取
        List<Product> cached = cacheService.getProductListFromCache(category);
        if (cached != null) {
            return cached;
        }
        
        // 从数据库获取
        List<Product> products = productRepository.findByCategory(category);
        
        // 缓存结果
        cacheService.cacheProductList(category, products, 60);
        
        return products;
    }
}
```

### 2. 缓存管理

```java
// 清除特定缓存
@CacheEvict(value = "product", key = "#productId")
public void updateProduct(Long productId, Product product) {
    productRepository.save(product);
}

// 清除所有缓存
@CacheEvict(value = "product", allEntries = true)
public void clearProductCache() {
    // 清除所有商品缓存
}
```

## 监控和维护

### 1. Redis监控命令

```bash
# 查看Redis信息
D:\softinstall\Redis\redis-cli.exe info

# 查看内存使用情况
D:\softinstall\Redis\redis-cli.exe info memory

# 查看数据库信息
D:\softinstall\Redis\redis-cli.exe info keyspace

# 查看连接数
D:\softinstall\Redis\redis-cli.exe info clients

# 查看慢查询
D:\softinstall\Redis\redis-cli.exe slowlog get 10
```

### 2. 缓存命中率监控

```bash
# 查看缓存统计
D:\softinstall\Redis\redis-cli.exe info stats

# 查看命令统计
D:\softinstall\Redis\redis-cli.exe info commandstats
```

### 3. 性能优化建议

1. **合理设置过期时间**: 根据数据更新频率设置合适的TTL
2. **使用合适的数据结构**: 根据业务需求选择String、Hash、List、Set等
3. **避免大key**: 单个key的值不要过大，建议小于1MB
4. **使用管道操作**: 批量操作时使用pipeline提高性能
5. **监控内存使用**: 定期检查Redis内存使用情况

## 故障排除

### 1. 常见问题

#### Redis连接失败
```bash
# 检查Redis是否启动
D:\softinstall\Redis\redis-cli.exe ping

# 检查端口是否被占用
netstat -an | findstr 6379

# 检查防火墙设置
```

#### Redis启动失败
```bash
# 检查Redis路径是否正确
dir D:\softinstall\Redis\redis-server.exe

# 检查配置文件
dir D:\softinstall\Redis\redis.windows.conf

# 手动启动并查看错误信息
D:\softinstall\Redis\redis-server.exe
```

#### 内存不足
```bash
# 查看内存使用
D:\softinstall\Redis\redis-cli.exe info memory

# 清理过期key
D:\softinstall\Redis\redis-cli.exe flushdb

# 设置内存策略
D:\softinstall\Redis\redis-cli.exe config set maxmemory-policy allkeys-lru
```

#### 性能问题
```bash
# 查看慢查询
D:\softinstall\Redis\redis-cli.exe slowlog get 10

# 查看命令统计
D:\softinstall\Redis\redis-cli.exe info commandstats

# 优化配置
D:\softinstall\Redis\redis-cli.exe config set save ""
```

### 2. 日志查看

```bash
# 查看Redis日志 (如果配置了日志文件)
type D:\softinstall\Redis\redis.log

# 查看应用日志中的缓存信息
grep "cache" application.log
```

## 安全配置

### 1. 基本安全设置

```bash
# 设置密码
D:\softinstall\Redis\redis-cli.exe config set requirepass "your_password"

# 禁用危险命令
D:\softinstall\Redis\redis-cli.exe config set rename-command FLUSHDB ""
D:\softinstall\Redis\redis-cli.exe config set rename-command FLUSHALL ""

# 绑定到特定IP
D:\softinstall\Redis\redis-cli.exe config set bind 127.0.0.1
```

### 2. 网络安全

```bash
# 只允许本地连接
D:\softinstall\Redis\redis-cli.exe config set bind 127.0.0.1

# 设置访问控制
D:\softinstall\Redis\redis-cli.exe config set protected-mode yes
```

## 备份和恢复

### 1. 数据备份

```bash
# 创建备份
D:\softinstall\Redis\redis-cli.exe bgsave

# 查看备份文件
dir D:\softinstall\Redis\dump.rdb
```

### 2. 数据恢复

```bash
# 停止Redis
D:\softinstall\Redis\redis-cli.exe shutdown

# 替换dump.rdb文件
copy backup.rdb D:\softinstall\Redis\dump.rdb

# 重启Redis
D:\softinstall\Redis\redis-server.exe
```

## 项目启动检查清单

### 启动前检查
- [ ] Redis服务器已启动 (`D:\softinstall\Redis\redis-server.exe`)
- [ ] Redis连接正常 (`D:\softinstall\Redis\redis-cli.exe ping` 返回 PONG)
- [ ] 端口6379未被占用
- [ ] 项目配置文件中的Redis配置正确

### 启动后验证
- [ ] 认证服务启动成功
- [ ] 商品服务启动成功
- [ ] 用户服务启动成功
- [ ] 缓存功能正常工作
- [ ] 各服务数据库分离正常 (DB 0, 1, 2)

## 总结

Redis缓存配置完成后，系统将获得以下优势：

1. **性能提升**: 减少数据库查询，提高响应速度
2. **用户体验**: 更快的页面加载和数据访问
3. **系统稳定性**: 减少数据库压力，提高系统稳定性
4. **可扩展性**: 支持水平扩展和负载均衡

建议定期监控缓存命中率和性能指标，根据实际使用情况调整缓存策略。 