# 支付服务API文档

## 1. 支付管理

### 1.1 创建支付订单
- 路径：`/api/v1/payments`
- 方法：POST
- 描述：创建支付订单
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "orderId": "long",      // 订单ID，必填
    "payType": "string",    // 支付方式，必填
    "payChannel": "string", // 支付渠道，必填
    "returnUrl": "string"   // 支付完成跳转地址，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "paymentId": "long",        // 支付ID
        "paymentNo": "string",      // 支付单号
        "orderId": "long",          // 订单ID
        "orderNo": "string",        // 订单编号
        "amount": "decimal",        // 支付金额
        "payType": "string",        // 支付方式
        "payChannel": "string",     // 支付渠道
        "status": "integer",        // 支付状态
        "payTime": "datetime",      // 支付时间
        "payInfo": {                // 支付信息
            "qrCode": "string",     // 二维码链接（扫码支付）
            "payUrl": "string",     // 支付链接（H5支付）
            "appPayInfo": "string"  // APP支付参数（APP支付）
        },
        "expireTime": "datetime"    // 支付过期时间
    }
}
```

### 1.2 获取支付订单
- 路径：`/api/v1/payments/{id}`
- 方法：GET
- 描述：获取支付订单信息
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "long",              // 支付ID
        "paymentNo": "string",     // 支付单号
        "orderId": "long",         // 订单ID
        "orderNo": "string",       // 订单编号
        "amount": "decimal",       // 支付金额
        "payType": "string",       // 支付方式
        "payChannel": "string",    // 支付渠道
        "status": "integer",       // 支付状态
        "payTime": "datetime",     // 支付时间
        "payInfo": {               // 支付信息
            "transactionId": "string", // 第三方交易号
            "payAccount": "string",    // 支付账号
            "payMethod": "string",     // 支付方式
            "payBank": "string"        // 支付银行
        },
        "createdAt": "datetime",   // 创建时间
        "updatedAt": "datetime"    // 更新时间
    }
}
```

### 1.3 查询支付状态
- 路径：`/api/v1/payments/{id}/status`
- 方法：GET
- 描述：查询支付订单状态
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "status": "integer",    // 支付状态
        "payTime": "datetime",  // 支付时间
        "payInfo": {            // 支付信息
            "transactionId": "string", // 第三方交易号
            "payAccount": "string",    // 支付账号
            "payMethod": "string",     // 支付方式
            "payBank": "string"        // 支付银行
        }
    }
}
```

### 1.4 取消支付
- 路径：`/api/v1/payments/{id}/cancel`
- 方法：POST
- 描述：取消支付订单
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "取消成功",
    "data": null
}
```

### 1.5 关闭支付
- 路径：`/api/v1/payments/{id}/close`
- 方法：POST
- 描述：关闭支付订单
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "关闭成功",
    "data": null
}
```

## 2. 退款管理

### 2.1 申请退款
- 路径：`/api/v1/payments/refunds`
- 方法：POST
- 描述：申请退款
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "paymentId": "long",     // 支付ID，必填
    "amount": "decimal",     // 退款金额，必填
    "reason": "string",      // 退款原因，必填
    "notifyUrl": "string"    // 退款结果通知地址，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "申请成功",
    "data": {
        "refundId": "long",        // 退款ID
        "refundNo": "string",      // 退款单号
        "paymentId": "long",       // 支付ID
        "paymentNo": "string",     // 支付单号
        "amount": "decimal",       // 退款金额
        "status": "integer",       // 退款状态
        "refundTime": "datetime",  // 退款时间
        "refundInfo": {            // 退款信息
            "transactionId": "string", // 第三方交易号
            "refundAccount": "string", // 退款账号
            "refundMethod": "string",  // 退款方式
            "refundBank": "string"     // 退款银行
        }
    }
}
```

### 2.2 查询退款
- 路径：`/api/v1/payments/refunds/{id}`
- 方法：GET
- 描述：查询退款信息
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "long",              // 退款ID
        "refundNo": "string",      // 退款单号
        "paymentId": "long",       // 支付ID
        "paymentNo": "string",     // 支付单号
        "amount": "decimal",       // 退款金额
        "status": "integer",       // 退款状态
        "reason": "string",        // 退款原因
        "refundTime": "datetime",  // 退款时间
        "refundInfo": {            // 退款信息
            "transactionId": "string", // 第三方交易号
            "refundAccount": "string", // 退款账号
            "refundMethod": "string",  // 退款方式
            "refundBank": "string"     // 退款银行
        },
        "createdAt": "datetime",   // 创建时间
        "updatedAt": "datetime"    // 更新时间
    }
}
```

### 2.3 取消退款
- 路径：`/api/v1/payments/refunds/{id}/cancel`
- 方法：POST
- 描述：取消退款申请
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "取消成功",
    "data": null
}
```

## 3. 支付渠道

### 3.1 获取支付渠道列表
- 路径：`/api/v1/payments/channels`
- 方法：GET
- 描述：获取可用的支付渠道列表
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": [{
        "code": "string",      // 渠道编码
        "name": "string",      // 渠道名称
        "icon": "string",      // 渠道图标
        "description": "string", // 渠道描述
        "payTypes": ["string"], // 支持的支付方式
        "status": "integer",   // 状态：0-禁用，1-启用
        "config": {            // 渠道配置
            "appId": "string", // 应用ID
            "merchantId": "string", // 商户ID
            "publicKey": "string",  // 公钥
            "privateKey": "string"  // 私钥
        }
    }]
}
```

### 3.2 获取支付方式列表
- 路径：`/api/v1/payments/types`
- 方法：GET
- 描述：获取可用的支付方式列表
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": [{
        "code": "string",      // 方式编码
        "name": "string",      // 方式名称
        "icon": "string",      // 方式图标
        "description": "string", // 方式描述
        "channels": ["string"], // 支持的渠道
        "status": "integer",   // 状态：0-禁用，1-启用
        "config": {            // 方式配置
            "minAmount": "decimal", // 最小金额
            "maxAmount": "decimal", // 最大金额
            "feeRate": "decimal",   // 手续费率
            "feeType": "string"     // 手续费类型
        }
    }]
}
```

## 4. 交易流水

### 4.1 获取交易流水列表
- 路径：`/api/v1/payments/transactions`
- 方法：GET
- 描述：获取交易流水列表
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "page": "integer",      // 页码，从1开始
    "size": "integer",      // 每页大小
    "type": "string",       // 交易类型，选填
    "status": "integer",    // 交易状态，选填
    "startTime": "datetime", // 开始时间，选填
    "endTime": "datetime"   // 结束时间，选填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": "long",          // 总记录数
        "pages": "integer",       // 总页数
        "current": "integer",     // 当前页
        "records": [{
            "id": "long",             // 流水ID
            "transactionNo": "string", // 流水号
            "type": "string",         // 交易类型
            "amount": "decimal",      // 交易金额
            "status": "integer",      // 交易状态
            "payType": "string",      // 支付方式
            "payChannel": "string",   // 支付渠道
            "orderId": "long",        // 关联订单ID
            "orderNo": "string",      // 关联订单号
            "transactionId": "string", // 第三方交易号
            "payAccount": "string",   // 支付账号
            "payMethod": "string",    // 支付方式
            "payBank": "string",      // 支付银行
            "createdAt": "datetime",  // 创建时间
            "updatedAt": "datetime"   // 更新时间
        }]
    }
}
```

### 4.2 获取交易流水详情
- 路径：`/api/v1/payments/transactions/{id}`
- 方法：GET
- 描述：获取交易流水详细信息
- 请求头：`Authorization: Bearer {token}`
- 响应结果：同交易流水列表中的单个记录

## 5. 对账管理

### 5.1 获取对账单
- 路径：`/api/v1/payments/reconciliation`
- 方法：GET
- 描述：获取对账单
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "date": "date",         // 对账日期，必填
    "channel": "string"     // 支付渠道，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "date": "date",           // 对账日期
        "channel": "string",      // 支付渠道
        "totalCount": "integer",  // 总笔数
        "totalAmount": "decimal", // 总金额
        "successCount": "integer", // 成功笔数
        "successAmount": "decimal", // 成功金额
        "failCount": "integer",   // 失败笔数
        "failAmount": "decimal",  // 失败金额
        "refundCount": "integer", // 退款笔数
        "refundAmount": "decimal", // 退款金额
        "details": [{            // 对账明细
            "transactionNo": "string", // 流水号
            "type": "string",         // 交易类型
            "amount": "decimal",      // 交易金额
            "status": "integer",      // 交易状态
            "transactionId": "string", // 第三方交易号
            "payTime": "datetime",    // 支付时间
            "reconciliationStatus": "integer" // 对账状态
        }]
    }
}
```

### 5.2 确认对账
- 路径：`/api/v1/payments/reconciliation/confirm`
- 方法：POST
- 描述：确认对账结果
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "date": "date",         // 对账日期，必填
    "channel": "string",    // 支付渠道，必填
    "status": "integer",    // 对账状态，必填
    "remark": "string"      // 备注，选填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "确认成功",
    "data": null
}
```

## 6. 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 4001 | 支付订单不存在 | 检查支付ID |
| 4002 | 支付订单已关闭 | 重新创建支付订单 |
| 4003 | 支付订单已过期 | 重新创建支付订单 |
| 4004 | 支付金额不匹配 | 检查支付金额 |
| 4005 | 支付渠道不可用 | 更换支付渠道 |
| 4006 | 支付方式不支持 | 更换支付方式 |
| 4007 | 退款金额超限 | 检查退款金额 |
| 4008 | 退款申请已存在 | 检查退款状态 |
| 4009 | 对账数据不存在 | 检查对账日期 |
| 4010 | 对账状态错误 | 检查对账状态 |

## 7. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 