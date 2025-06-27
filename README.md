# 商城系统 (Mall)

一个基于微服务架构的电商系统，包含前端商城、管理员后台和多个微服务。

## 系统架构

### 微服务
- **认证服务** (8081): 用户登录、注册、权限验证
- **商品服务** (8082): 商品管理、搜索、购物车
- **用户服务** (8085): 用户信息管理

### 前端
- **商城前端**: 用户购物界面
- **管理员后台**: 商品、用户、订单管理

### 反向代理
- **Nginx**: 统一API入口，解决跨域问题

## 快速开始

### 1. 环境要求
- Java 8+
- Maven 3.6+
- MySQL 5.7+
- Nginx 1.22.0+

### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE mall_auth;
CREATE DATABASE mall_product;
CREATE DATABASE mall_user;
```

### 3. 启动微服务
```bash
# 启动认证服务
cd auth-service && mvn spring-boot:run

# 启动商品服务
cd product-service && mvn spring-boot:run

# 启动用户服务
cd user-service && mvn spring-boot:run
```

### 4. 启动Nginx反向代理
```bash
# 一键启动完整系统
start-mall-system.bat

# 或分别启动
start-nginx-mall.bat
```

### 5. 访问系统
- **前端商城**: http://localhost
- **管理员后台**: http://localhost/admin
- **健康检查**: http://localhost/health

## API接口

所有API通过nginx统一代理：

| 服务 | 路径 | 说明 |
|------|------|------|
| 认证服务 | `/api/auth/` | 登录、注册、权限验证 |
| 商品服务 | `/api/products/` | 商品管理、搜索 |
| 首页商品 | `/api/homepage/` | 首页商品管理 |
| 用户服务 | `/api/user/` | 用户信息管理 |
| 购物车 | `/api/cart/` | 购物车功能 |

## 管理命令

### Nginx管理
```bash
# 启动nginx
start-nginx-mall.bat

# 停止nginx
stop-nginx-mall.bat

# 测试配置
test-nginx-mall.bat

# 检查配置
check-nginx-config.bat
```

### 系统管理
```bash
# 启动完整系统
start-mall-system.bat

# 设置nginx解决方案
setup-mall-nginx.bat
```

## 功能特性

### 商城前端
- 商品浏览和搜索
- 购物车管理
- 用户登录注册
- 商品详情页

### 管理员后台
- 商品管理（增删改查）
- 首页商品管理
- 用户管理
- 订单管理
- 分类管理
- 统计报表

### 技术特性
- 微服务架构
- RESTful API
- JWT认证
- 跨域支持
- 反向代理
- 响应式设计

## 配置说明

### Nginx配置
- 配置文件: `nginx/conf/nginx-mall.conf`
- 安装路径: `D:\softinstall\nginx\nginx-1.22.0-tlias`
- 日志文件: `logs/mall-access.log`, `logs/mall-error.log`

### 前端配置
- API配置: `frontend/js/api-config.js`
- 使用nginx代理地址: `http://localhost/api/`

## 故障排除

### 常见问题
1. **nginx启动失败**
   - 检查端口80是否被占用
   - 验证nginx安装路径
   - 检查配置文件语法

2. **API请求失败**
   - 确保所有微服务已启动
   - 检查nginx代理配置
   - 查看nginx错误日志

3. **跨域问题**
   - 确保使用nginx代理地址
   - 检查CORS配置
   - 清除浏览器缓存

### 调试命令
```bash
# 检查nginx配置
nginx.exe -t -c nginx-mall.conf

# 查看nginx进程
tasklist /fi "imagename eq nginx.exe"

# 测试API代理
curl http://localhost/api/auth/health
curl http://localhost/api/products/health
```

## 开发指南

### 添加新功能
1. 在对应微服务中实现API
2. 在nginx配置中添加代理路由
3. 在前端添加相应的界面和逻辑

### 部署说明
1. 确保所有微服务正常运行
2. 启动nginx反向代理
3. 测试所有功能正常

## 联系支持

如有问题，请提供：
1. 错误日志
2. 系统状态
3. 具体错误现象