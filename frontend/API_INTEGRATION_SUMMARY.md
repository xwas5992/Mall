# API集成总结文档

## 已修复的问题

### 1. 端口冲突问题
- **问题**: `auth-service` 和 `product-service` 都配置在8082端口
- **解决**: 将 `auth-service` 端口修改为8081
- **当前端口配置**:
  - `auth-service`: 8081
  - `product-service`: 8082  
  - `user-service`: 8085

### 2. 前端API配置问题
- **问题**: `api-config.js` 配置错误，URL格式不正确
- **解决**: 重写 `api-config.js`，提供正确的微服务基地址
- **配置**:
  ```javascript
  window.API_CONFIG = {
      auth: { baseUrl: 'http://localhost:8081/api/auth' },
      product: { baseUrl: 'http://localhost:8082/api/products' },
      user: { baseUrl: 'http://localhost:8085/api/user' }
  };
  ```

### 3. 前端API调用修正
- **首页** (`frontend/home/main.js`): 修正商品分类API调用
- **详情页** (`frontend/detail/detail.js`): 修正商品详情API调用
- **搜索页** (`frontend/search/search.js`): 修正搜索API调用
- **登录页** (`frontend/login/login.js`): 修正登录/注册API调用
- **认证管理** (`frontend/js/auth.js`): 修正统一认证API调用

### 4. 后端CORS配置
- **问题**: `auth-service` 缺少CORS配置
- **解决**: 在 `SecurityConfig` 中添加CORS配置
- **状态**: `product-service` 已有CORS配置

### 5. 数据库表名不匹配
- **问题**: 实体类表名与数据库设计文档不匹配
- **解决**: 
  - `auth-service` User实体: `users` → `auth_user`
  - `product-service` Product实体: `products` → `product`

### 6. 前端响应处理修正
- **问题**: 前端期望包装的响应格式，但后端直接返回数据
- **解决**: 修正前端处理逻辑，直接处理 `{token, tokenType, user}` 格式

### 7. 注册数据格式修正
- **问题**: 前端发送的注册数据与后端DTO不匹配
- **解决**: 修正前端注册请求，添加必需的 `email` 和 `fullName` 字段

## 启动步骤

### 1. 数据库准备
确保MySQL服务运行，并创建以下数据库：
```sql
CREATE DATABASE mall_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE mall_product CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE mall_user CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 启动微服务
按以下顺序启动服务：

1. **启动 auth-service** (端口8081)
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```

2. **启动 product-service** (端口8082)
   ```bash
   cd product-service
   mvn spring-boot:run
   ```

3. **启动 user-service** (端口8085)
   ```bash
   cd user-service
   mvn spring-boot:run
   ```

### 3. 验证服务状态
访问以下URL验证服务是否正常启动：
- `http://localhost:8081/actuator/health` - 认证服务健康检查
- `http://localhost:8082/actuator/health` - 商品服务健康检查
- `http://localhost:8085/actuator/health` - 用户服务健康检查

### 4. 前端访问
启动本地Web服务器访问前端：
- 首页: `http://localhost:8080/frontend/home/index.html`
- 登录页: `http://localhost:8080/frontend/login/login.html`
- 搜索页: `http://localhost:8080/frontend/search/search.html`

## 已知限制

### 1. 验证码功能
- **状态**: 暂时禁用
- **原因**: 后端没有发送验证码的接口
- **影响**: 注册时不需要输入验证码

### 2. 用户服务
- **状态**: 暂时未使用
- **原因**: 认证功能统一由 `auth-service` 处理
- **影响**: 无

### 3. 数据初始化
- **状态**: 需要手动添加测试数据
- **建议**: 在数据库中插入一些测试商品和用户数据

## 测试建议

### 1. 功能测试
1. 用户注册和登录
2. 商品列表展示
3. 商品搜索功能
4. 商品详情查看
5. 购物车功能

### 2. API测试
使用Postman或浏览器测试以下API：
- `POST http://localhost:8081/api/auth/register`
- `POST http://localhost:8081/api/auth/login`
- `GET http://localhost:8082/api/products`
- `GET http://localhost:8082/api/products/{id}`

## 故障排除

### 1. 端口占用
如果端口被占用，检查并关闭占用进程：
```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -i :8081
kill -9 <进程ID>
```

### 2. 数据库连接失败
检查MySQL服务状态和连接配置：
- 确认MySQL服务运行
- 验证用户名密码正确
- 检查数据库是否存在

### 3. CORS错误
如果出现CORS错误，检查：
- 后端CORS配置是否正确
- 前端请求URL是否正确
- 浏览器控制台错误信息

### 4. 前端资源加载失败
检查：
- 本地资源文件路径是否正确
- Bootstrap和Font Awesome文件是否存在
- 浏览器网络面板错误信息 