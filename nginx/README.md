# 商城项目 Nginx 配置

## 概述

本项目使用 Nginx 作为反向代理服务器，解决跨域问题并提供统一的API访问入口。

## 文件结构

```
nginx/
├── conf/
│   ├── nginx.conf              # Linux版本配置
│   ├── nginx-windows.conf      # Windows版本配置
│   └── nginx-mall.conf         # 商城项目专用配置
├── README.md                   # 本文档
└── 项目根目录/
    ├── start-nginx-mall.bat    # 启动nginx
    ├── stop-nginx-mall.bat     # 停止nginx
    ├── test-nginx-mall.bat     # 测试配置
    └── setup-mall-nginx.bat    # 完整解决方案
```

## 快速开始

### 1. 环境要求

- Nginx 1.22.0 或更高版本
- 已安装的路径：`D:\softinstall\nginx\nginx-1.22.0-tlias`
- 所有微服务正常运行（认证服务8081、商品服务8082、用户服务8085）

### 2. 一键部署

运行完整解决方案脚本：

```bash
setup-mall-nginx.bat
```

此脚本会自动：
- 检查nginx安装路径
- 验证配置文件语法
- 检查微服务状态
- 启动nginx
- 测试所有API代理

### 3. 手动操作

#### 启动nginx
```bash
start-nginx-mall.bat
```

#### 停止nginx
```bash
stop-nginx-mall.bat
```

#### 测试配置
```bash
test-nginx-mall.bat
```

## 配置说明

### 主要功能

1. **反向代理**：将所有API请求代理到对应的微服务
2. **CORS支持**：解决跨域访问问题
3. **静态文件服务**：提供前端文件访问
4. **负载均衡**：支持多实例部署（当前为单实例）

### API路由配置

| 路径 | 代理目标 | 说明 |
|------|----------|------|
| `/api/auth/` | `http://127.0.0.1:8081/api/auth/` | 认证服务 |
| `/api/products/` | `http://127.0.0.1:8082/product-service/api/products/` | 商品服务 |
| `/api/homepage/` | `http://127.0.0.1:8082/product-service/api/homepage/` | 首页商品管理 |
| `/api/user/` | `http://127.0.0.1:8085/api/user/` | 用户服务 |
| `/api/cart/` | `http://127.0.0.1:8082/product-service/api/cart/` | 购物车 |

### 前端访问

| 路径 | 说明 |
|------|------|
| `http://localhost` | 前端商城 |
| `http://localhost/admin` | 管理员后台 |
| `http://localhost/health` | 健康检查 |

## CORS配置

nginx配置了完整的CORS支持：

```nginx
# 全局CORS头
add_header Access-Control-Allow-Origin "*" always;
add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
add_header Access-Control-Allow-Headers "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization" always;

# OPTIONS预检请求处理
if ($request_method = 'OPTIONS') {
    add_header Access-Control-Allow-Origin "*" always;
    add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
    add_header Access-Control-Allow-Headers "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization" always;
    add_header Access-Control-Max-Age 1728000 always;
    return 204;
}
```

## 日志管理

nginx日志文件位置：
- 访问日志：`D:\softinstall\nginx\nginx-1.22.0-tlias\logs\mall-access.log`
- 错误日志：`D:\softinstall\nginx\nginx-1.22.0-tlias\logs\mall-error.log`

## 故障排除

### 常见问题

1. **nginx启动失败**
   - 检查端口80是否被占用
   - 检查配置文件语法：`nginx.exe -t -c nginx-mall.conf`
   - 检查nginx安装路径是否正确

2. **API代理失败**
   - 确保所有微服务都在运行
   - 检查防火墙设置
   - 查看nginx错误日志

3. **CORS问题**
   - 确保前端使用正确的API地址（http://localhost/api/...）
   - 检查浏览器控制台错误信息
   - 验证nginx CORS配置

### 调试命令

```bash
# 检查nginx配置语法
nginx.exe -t -c nginx-mall.conf

# 检查nginx进程
tasklist /fi "imagename eq nginx.exe"

# 查看nginx日志
tail -f logs/mall-error.log

# 测试API代理
curl http://localhost/api/auth/health
curl http://localhost/api/products/health
curl http://localhost/api/homepage/products
```

## 性能优化

1. **GZIP压缩**：已启用，减少传输大小
2. **连接池**：配置了keepalive连接
3. **缓存策略**：静态文件缓存7天
4. **超时设置**：合理的连接和读取超时

## 安全配置

1. **隐藏版本信息**：`server_tokens off`
2. **安全头部**：X-Frame-Options、X-XSS-Protection等
3. **CORS控制**：允许必要的跨域访问
4. **代理头部**：正确设置X-Real-IP等头部

## 扩展配置

如需添加新的微服务或API路由，请修改 `nginx-mall.conf` 文件：

1. 添加上游服务器配置
2. 添加location块
3. 重启nginx服务

## 联系支持

如遇到问题，请提供：
1. nginx错误日志
2. 微服务状态
3. 浏览器控制台错误
4. 具体的错误现象描述 