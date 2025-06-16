# API文档概述

## 1. API规范

### 1.1 基础规范
- 基础路径：`/api/v1`
- 请求方式：GET、POST、PUT、DELETE
- 数据格式：JSON
- 字符编码：UTF-8
- 时间格式：ISO 8601（yyyy-MM-dd'T'HH:mm:ss.SSSZ）

### 1.2 认证方式
- 认证头：`Authorization: Bearer {token}`
- Token获取：`/api/v1/auth/login`
- Token刷新：`/api/v1/auth/refresh`

### 1.3 响应格式
```json
{
    "code": 200,           // 状态码
    "message": "success",  // 状态信息
    "data": {             // 响应数据
        // 具体数据
    },
    "timestamp": "2024-03-xxTxx:xx:xx.xxxZ"  // 时间戳
}
```

### 1.4 错误码说明
| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查请求参数 |
| 401 | 未认证 | 重新登录获取token |
| 403 | 无权限 | 检查用户权限 |
| 404 | 资源不存在 | 检查请求路径 |
| 500 | 服务器错误 | 联系管理员 |

## 2. 服务API列表

### 2.1 用户服务（User Service）
- 基础路径：`/api/v1/users`
- 主要接口：
  - 用户注册
  - 用户登录
  - 用户信息
  - 地址管理
  - 会员管理
- [详细文档](./user-service.md)

### 2.2 商品服务（Product Service）
- 基础路径：`/api/v1/products`
- 主要接口：
  - 商品管理
  - 分类管理
  - 库存管理
  - 商品搜索
  - 商品评价
- [详细文档](./product-service.md)

### 2.3 订单服务（Order Service）
- 基础路径：`/api/v1/orders`
- 主要接口：
  - 订单管理
  - 购物车管理
  - 订单支付
  - 售后服务
  - 订单评价
- [详细文档](./order-service.md)

### 2.4 支付服务（Payment Service）
- 基础路径：`/api/v1/payments`
- 主要接口：
  - 支付管理
  - 退款管理
  - 支付渠道
  - 交易流水
  - 对账管理
- [详细文档](./payment-service.md)

## 3. 通用接口

### 3.1 文件上传
- 路径：`/api/v1/files/upload`
- 方法：POST
- 说明：支持图片、文档等文件上传

### 3.2 短信验证
- 路径：`/api/v1/sms/send`
- 方法：POST
- 说明：发送短信验证码

### 3.3 系统配置
- 路径：`/api/v1/configs`
- 方法：GET
- 说明：获取系统配置信息

## 4. 接口安全

### 4.1 访问控制
- 接口认证
- 权限验证
- 数据权限
- 操作审计

### 4.2 安全措施
- 参数验证
- SQL注入防护
- XSS防护
- CSRF防护
- 敏感数据加密

### 4.3 限流措施
- 接口限流
- IP限流
- 用户限流
- 业务限流

## 5. 接口测试

### 5.1 测试环境
- 开发环境：`http://dev-api.mall.com`
- 测试环境：`http://test-api.mall.com`
- 生产环境：`https://api.mall.com`

### 5.2 测试工具
- Postman
- Swagger UI
- JMeter
- curl

### 5.3 测试账号
| 环境 | 账号 | 密码 | 角色 |
|------|------|------|------|
| 开发 | dev_user | dev123 | 普通用户 |
| 测试 | test_user | test123 | 普通用户 |
| 生产 | - | - | 需要申请 |

## 6. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 