# Redis 使用说明

## 概述

本项目使用Redis作为缓存中间件，所有服务共享同一个Redis实例（默认数据库DB 0），通过不同的key前缀来区分不同服务的数据。

## 快速启动

### 1. 启动Redis
```bash
# 使用项目脚本
./start-redis-simple.bat

# 或手动启动
cd D:\softinstall\Redis
redis-server.exe
```

### 2. 测试Redis
```bash
# 测试连接
D:\softinstall\Redis\redis-cli.exe ping

# 运行测试脚本
./test-redis-simple.bat
```

## 服务配置

所有服务都使用Redis默认数据库 (DB 0)，通过key前缀区分：

- **认证服务**: `auth:` 前缀
- **商品服务**: `product:` 前缀  
- **用户服务**: `user:` 前缀

## 配置文件

- `redis.conf` - Redis配置文件
- `start-redis-simple.bat` - Redis启动脚本
- `test-redis-simple.bat` - Redis测试脚本

## 常用命令

```bash
# 查看所有缓存
D:\softinstall\Redis\redis-cli.exe keys "*"

# 查看各服务缓存
D:\softinstall\Redis\redis-cli.exe keys "auth:*"
D:\softinstall\Redis\redis-cli.exe keys "product:*"
D:\softinstall\Redis\redis-cli.exe keys "user:*"

# 清理所有缓存
D:\softinstall\Redis\redis-cli.exe flushall

# 查看Redis信息
D:\softinstall\Redis\redis-cli.exe info
```

## 注意事项

1. 所有服务共享同一个Redis实例
2. 通过key前缀区分不同服务的数据
3. 使用默认数据库DB 0
4. 配置文件已优化适合Windows环境 