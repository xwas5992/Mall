# 商城管理系统 - API文档

## 概述

本文档详细说明了商城管理系统的所有API接口，包括认证服务、商品服务、用户服务等。

## 基础信息

- **认证方式**: JWT Token
- **请求格式**: JSON
- **响应格式**: JSON
- **字符编码**: UTF-8

## 服务端口

- **认证服务 (auth-service)**: 8081
- **商品服务 (product-service)**: 8082
- **用户服务 (user-service)**: 8085

## 认证服务 API

### 基础路径: `/api/auth`

#### 1. 用户登录
- **URL**: `POST /api/auth/login`
- **描述**: 用户登录获取Token
- **请求体**:
  ```json
  {
    "username": "admin",
    "password": "password"
  }
  ```
- **响应**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "admin",
      "role": "ADMIN"
    }
  }
  ```

#### 2. 用户注册
- **URL**: `POST /api/auth/register`
- **描述**: 新用户注册
- **参数**: `code` (验证码)
- **请求体**:
  ```json
  {
    "username": "newuser",
    "password": "password",
    "fullName": "张三"
  }
  ```

#### 3. 发送验证码
- **URL**: `POST /api/auth/send-code`
- **描述**: 向指定手机号发送验证码
- **参数**: `phone` (手机号)

#### 4. 验证Token
- **URL**: `GET /api/auth/verify`
- **描述**: 验证JWT Token的有效性
- **Headers**: `Authorization: Bearer <token>`
- **响应**:
  ```json
  {
    "valid": true,
    "username": "admin",
    "role": "ADMIN",
    "fullName": "管理员"
  }
  ```

#### 5. 获取用户列表 (管理员)
- **URL**: `GET /api/auth/users`
- **描述**: 管理员获取所有用户列表（分页）
- **权限**: ADMIN
- **参数**: 
  - `page` (页码，默认0)
  - `size` (每页大小，默认10)
- **Headers**: `Authorization: Bearer <token>`

#### 6. 获取用户总数 (管理员)
- **URL**: `GET /api/auth/users/count`
- **描述**: 获取系统用户总数
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`

#### 7. 获取用户详情 (管理员)
- **URL**: `GET /api/auth/users/{id}`
- **描述**: 根据ID获取用户详细信息
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`

#### 8. 更新用户状态 (管理员)
- **URL**: `PUT /api/auth/users/{id}/status`
- **描述**: 启用或禁用用户账户
- **权限**: ADMIN
- **参数**: `enabled` (是否启用)
- **Headers**: `Authorization: Bearer <token>`

#### 9. 删除用户 (管理员)
- **URL**: `DELETE /api/auth/users/{id}`
- **描述**: 删除指定用户
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`

#### 10. 健康检查
- **URL**: `GET /api/auth/health`
- **描述**: 服务健康状态检查
- **响应**:
  ```json
  {
    "status": "UP",
    "service": "auth-service",
    "timestamp": 1703123456789
  }
  ```

## 商品服务 API

### 基础路径: `/api/products`

#### 1. 创建商品 (管理员)
- **URL**: `POST /api/products`
- **描述**: 创建一个新的商品
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`
- **请求体**:
  ```json
  {
    "name": "iPhone 15 Pro",
    "description": "最新款iPhone",
    "price": 8999.00,
    "stock": 100,
    "brand": "Apple",
    "categoryId": 1,
    "imageUrl": "https://example.com/image.jpg",
    "status": true
  }
  ```

#### 2. 获取商品详情
- **URL**: `GET /api/products/{id}`
- **描述**: 根据ID获取商品详细信息

#### 3. 获取商品列表
- **URL**: `GET /api/products`
- **描述**: 分页获取商品列表
- **参数**:
  - `page` (页码，默认0)
  - `size` (每页大小，默认10)
  - `sortBy` (排序字段，默认id)
  - `direction` (排序方向，默认DESC)

#### 4. 获取上架商品
- **URL**: `GET /api/products/active`
- **描述**: 分页获取所有上架商品
- **参数**:
  - `page` (页码，默认0)
  - `size` (每页大小，默认10)

#### 5. 关键词搜索商品
- **URL**: `GET /api/products/search`
- **描述**: 根据关键词搜索商品
- **参数**:
  - `keyword` (搜索关键词)
  - `page` (页码，默认0)
  - `size` (每页大小，默认10)

#### 6. 按分类搜索商品
- **URL**: `GET /api/products/search/category`
- **描述**: 根据分类搜索商品
- **参数**:
  - `category` (商品分类)
  - `page` (页码，默认0)
  - `size` (每页大小，默认10)

#### 7. 按品牌搜索商品
- **URL**: `GET /api/products/search/brand`
- **描述**: 根据品牌搜索商品
- **参数**:
  - `brand` (商品品牌)
  - `page` (页码，默认0)
  - `size` (每页大小，默认10)

#### 8. 按价格范围搜索商品
- **URL**: `GET /api/products/search/price`
- **描述**: 根据价格范围搜索商品
- **参数**:
  - `minPrice` (最低价格)
  - `maxPrice` (最高价格)
  - `page` (页码，默认0)
  - `size` (每页大小，默认10)

#### 9. 多条件搜索商品
- **URL**: `GET /api/products/search/filters`
- **描述**: 根据多个条件搜索商品
- **参数**:
  - `keyword` (搜索关键词，可选)
  - `category` (商品分类，可选)
  - `categoryId` (商品分类ID，可选)
  - `brand` (商品品牌，可选)
  - `minPrice` (最低价格，可选)
  - `maxPrice` (最高价格，可选)
  - `page` (页码，默认0)
  - `size` (每页大小，默认10)

#### 10. 更新商品 (管理员)
- **URL**: `PUT /api/products/{id}`
- **描述**: 根据ID更新商品信息
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`

#### 11. 删除商品 (管理员)
- **URL**: `DELETE /api/products/{id}`
- **描述**: 根据ID删除商品
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`

#### 12. 获取商品总数 (管理员)
- **URL**: `GET /api/products/count`
- **描述**: 获取系统商品总数
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`

#### 13. 获取所有分类 (管理员)
- **URL**: `GET /api/products/categories`
- **描述**: 获取所有商品分类
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`

#### 14. 获取所有品牌 (管理员)
- **URL**: `GET /api/products/brands`
- **描述**: 获取所有商品品牌
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`

#### 15. 获取商品统计 (管理员)
- **URL**: `GET /api/products/statistics`
- **描述**: 获取商品相关统计数据
- **权限**: ADMIN
- **Headers**: `Authorization: Bearer <token>`
- **响应**:
  ```json
  {
    "totalProducts": 150,
    "activeProducts": 120,
    "inactiveProducts": 30,
    "lowStockProducts": 5,
    "categoryStatistics": [
      {
        "category": "电子产品",
        "count": 50
      }
    ],
    "brandStatistics": [
      {
        "brand": "Apple",
        "count": 20
      }
    ]
  }
  ```

#### 16. 健康检查
- **URL**: `GET /api/products/health`
- **描述**: 服务健康状态检查

## 用户服务 API

### 基础路径: `/api/user`

#### 1. 用户注册
- **URL**: `POST /api/user/register`
- **描述**: 新用户注册
- **请求体**:
  ```json
  {
    "username": "user",
    "password": "password"
  }
  ```

#### 2. 用户登录
- **URL**: `POST /api/user/login`
- **描述**: 用户登录验证
- **参数**:
  - `username` (用户名)
  - `password` (密码)

#### 3. 获取用户列表
- **URL**: `GET /api/user/list`
- **描述**: 获取所有用户列表

#### 4. 健康检查
- **URL**: `GET /api/user/health`
- **描述**: 服务健康状态检查

## 错误响应格式

### 通用错误响应
```json
{
  "timestamp": "2024-01-01T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "错误描述信息",
  "path": "/api/endpoint"
}
```

### 常见HTTP状态码

- **200**: 请求成功
- **201**: 创建成功
- **400**: 请求参数错误
- **401**: 未授权
- **403**: 权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

## 认证和授权

### JWT Token格式
```
Authorization: Bearer <token>
```

### Token验证
所有需要认证的接口都需要在请求头中包含有效的JWT Token。

### 角色权限
- **ADMIN**: 管理员权限，可以访问所有接口
- **USER**: 普通用户权限，只能访问部分接口

## 分页响应格式

### 分页数据结构
```json
{
  "content": [
    // 数据列表
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "empty": false
}
```

## 使用示例

### 1. 管理员登录
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

### 2. 获取商品列表
```bash
curl -X GET "http://localhost:8082/api/products?page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

### 3. 创建商品
```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "测试商品",
    "description": "商品描述",
    "price": 99.99,
    "stock": 100,
    "brand": "测试品牌",
    "categoryId": 1,
    "imageUrl": "https://example.com/image.jpg",
    "status": true
  }'
```

## 注意事项

1. **CORS配置**: 所有服务都已配置CORS，支持跨域请求
2. **数据验证**: 所有输入数据都会进行验证
3. **错误处理**: 统一的错误处理机制
4. **日志记录**: 所有API调用都会记录日志
5. **安全防护**: 包含XSS、CSRF等安全防护措施

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础CRUD操作
- 添加管理员权限控制
- 完善API文档 