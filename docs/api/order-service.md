# 订单服务API文档

## 1. 购物车管理

### 1.1 获取购物车列表
- 路径：`/api/v1/cart`
- 方法：GET
- 描述：获取当前用户的购物车列表
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": [{
        "id": "long",             // 购物车项ID
        "productId": "long",      // 商品ID
        "productName": "string",  // 商品名称
        "skuId": "long",          // SKU ID
        "sku": {                  // SKU信息
            "specs": {            // 规格值
                "规格名": "规格值"
            },
            "price": "decimal",   // 价格
            "image": "string"     // 图片
        },
        "quantity": "integer",    // 数量
        "selected": "boolean",    // 是否选中
        "createdAt": "datetime",  // 创建时间
        "updatedAt": "datetime"   // 更新时间
    }]
}
```

### 1.2 添加商品到购物车
- 路径：`/api/v1/cart`
- 方法：POST
- 描述：添加商品到购物车
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "productId": "long",    // 商品ID，必填
    "skuId": "long",        // SKU ID，必填
    "quantity": "integer"   // 数量，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "添加成功",
    "data": {
        "id": "long"  // 购物车项ID
    }
}
```

### 1.3 更新购物车商品数量
- 路径：`/api/v1/cart/{id}/quantity`
- 方法：PUT
- 描述：更新购物车商品数量
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "quantity": "integer"  // 数量，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

### 1.4 删除购物车商品
- 路径：`/api/v1/cart/{id}`
- 方法：DELETE
- 描述：删除购物车商品
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

### 1.5 清空购物车
- 路径：`/api/v1/cart/clear`
- 方法：DELETE
- 描述：清空购物车
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "清空成功",
    "data": null
}
```

### 1.6 选择/取消选择商品
- 路径：`/api/v1/cart/{id}/select`
- 方法：PUT
- 描述：选择或取消选择购物车商品
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "selected": "boolean"  // 是否选中，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

## 2. 订单管理

### 2.1 创建订单
- 路径：`/api/v1/orders`
- 方法：POST
- 描述：创建新订单
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "addressId": "long",     // 收货地址ID，必填
    "items": [{             // 订单商品列表，必填
        "productId": "long", // 商品ID
        "skuId": "long",     // SKU ID
        "quantity": "integer" // 数量
    }],
    "remark": "string",      // 订单备注，选填
    "couponId": "long"       // 优惠券ID，选填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "orderId": "long",           // 订单ID
        "orderNo": "string",         // 订单编号
        "totalAmount": "decimal",    // 订单总金额
        "payAmount": "decimal",      // 实付金额
        "payTime": "datetime"        // 支付截止时间
    }
}
```

### 2.2 获取订单列表
- 路径：`/api/v1/orders`
- 方法：GET
- 描述：获取用户订单列表
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "page": "integer",      // 页码，从1开始
    "size": "integer",      // 每页大小
    "status": "integer",    // 订单状态，选填
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
            "id": "long",             // 订单ID
            "orderNo": "string",      // 订单编号
            "status": "integer",      // 订单状态
            "totalAmount": "decimal", // 订单总金额
            "payAmount": "decimal",   // 实付金额
            "payTime": "datetime",    // 支付时间
            "deliveryTime": "datetime", // 发货时间
            "completeTime": "datetime", // 完成时间
            "items": [{              // 订单商品列表
                "id": "long",        // 订单项ID
                "productId": "long", // 商品ID
                "productName": "string", // 商品名称
                "skuId": "long",     // SKU ID
                "sku": {             // SKU信息
                    "specs": {       // 规格值
                        "规格名": "规格值"
                    },
                    "image": "string" // 图片
                },
                "price": "decimal",  // 单价
                "quantity": "integer", // 数量
                "amount": "decimal"  // 小计金额
            }],
            "address": {             // 收货地址
                "receiver": "string", // 收货人
                "phone": "string",   // 手机号
                "province": "string", // 省份
                "city": "string",    // 城市
                "district": "string", // 区县
                "detail": "string"   // 详细地址
            },
            "createdAt": "datetime", // 创建时间
            "updatedAt": "datetime"  // 更新时间
        }]
    }
}
```

### 2.3 获取订单详情
- 路径：`/api/v1/orders/{id}`
- 方法：GET
- 描述：获取订单详细信息
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "long",             // 订单ID
        "orderNo": "string",      // 订单编号
        "status": "integer",      // 订单状态
        "totalAmount": "decimal", // 订单总金额
        "payAmount": "decimal",   // 实付金额
        "payTime": "datetime",    // 支付时间
        "payType": "string",      // 支付方式
        "deliveryTime": "datetime", // 发货时间
        "deliveryCompany": "string", // 物流公司
        "deliveryNo": "string",   // 物流单号
        "completeTime": "datetime", // 完成时间
        "cancelTime": "datetime", // 取消时间
        "cancelReason": "string", // 取消原因
        "items": [{              // 订单商品列表
            "id": "long",        // 订单项ID
            "productId": "long", // 商品ID
            "productName": "string", // 商品名称
            "skuId": "long",     // SKU ID
            "sku": {             // SKU信息
                "specs": {       // 规格值
                    "规格名": "规格值"
                },
                "image": "string" // 图片
            },
            "price": "decimal",  // 单价
            "quantity": "integer", // 数量
            "amount": "decimal", // 小计金额
            "refundStatus": "integer" // 退款状态
        }],
        "address": {             // 收货地址
            "receiver": "string", // 收货人
            "phone": "string",   // 手机号
            "province": "string", // 省份
            "city": "string",    // 城市
            "district": "string", // 区县
            "detail": "string"   // 详细地址
        },
        "coupon": {              // 优惠券信息
            "id": "long",        // 优惠券ID
            "name": "string",    // 优惠券名称
            "amount": "decimal"  // 优惠金额
        },
        "createdAt": "datetime", // 创建时间
        "updatedAt": "datetime"  // 更新时间
    }
}
```

### 2.4 取消订单
- 路径：`/api/v1/orders/{id}/cancel`
- 方法：POST
- 描述：取消订单
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "reason": "string"  // 取消原因，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "取消成功",
    "data": null
}
```

### 2.5 确认收货
- 路径：`/api/v1/orders/{id}/confirm`
- 方法：POST
- 描述：确认收货
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "确认成功",
    "data": null
}
```

### 2.6 删除订单
- 路径：`/api/v1/orders/{id}`
- 方法：DELETE
- 描述：删除订单
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

## 3. 退款管理

### 3.1 申请退款
- 路径：`/api/v1/orders/{id}/refund`
- 方法：POST
- 描述：申请退款
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "type": "integer",      // 退款类型：1-仅退款，2-退货退款，必填
    "reason": "string",     // 退款原因，必填
    "description": "string", // 问题描述，必填
    "images": ["string"],   // 图片凭证，选填
    "items": [{            // 退款商品列表，必填
        "orderItemId": "long", // 订单项ID
        "quantity": "integer", // 退款数量
        "reason": "string",   // 退款原因
        "description": "string", // 问题描述
        "images": ["string"]  // 图片凭证
    }]
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "申请成功",
    "data": {
        "refundId": "long",        // 退款ID
        "refundNo": "string",      // 退款编号
        "amount": "decimal",       // 退款金额
        "status": "integer"        // 退款状态
    }
}
```

### 3.2 获取退款列表
- 路径：`/api/v1/orders/refunds`
- 方法：GET
- 描述：获取退款列表
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "page": "integer",      // 页码，从1开始
    "size": "integer",      // 每页大小
    "status": "integer",    // 退款状态，选填
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
            "id": "long",             // 退款ID
            "refundNo": "string",     // 退款编号
            "orderId": "long",        // 订单ID
            "orderNo": "string",      // 订单编号
            "type": "integer",        // 退款类型
            "amount": "decimal",      // 退款金额
            "status": "integer",      // 退款状态
            "reason": "string",       // 退款原因
            "description": "string",  // 问题描述
            "images": ["string"],     // 图片凭证
            "items": [{              // 退款商品列表
                "orderItemId": "long", // 订单项ID
                "productName": "string", // 商品名称
                "sku": {             // SKU信息
                    "specs": {       // 规格值
                        "规格名": "规格值"
                    },
                    "image": "string" // 图片
                },
                "quantity": "integer", // 退款数量
                "amount": "decimal",  // 退款金额
                "reason": "string",   // 退款原因
                "description": "string", // 问题描述
                "images": ["string"]  // 图片凭证
            }],
            "logistics": {           // 退货物流信息
                "company": "string", // 物流公司
                "trackingNo": "string", // 物流单号
                "deliveryTime": "datetime" // 发货时间
            },
            "createdAt": "datetime", // 创建时间
            "updatedAt": "datetime"  // 更新时间
        }]
    }
}
```

### 3.3 获取退款详情
- 路径：`/api/v1/orders/refunds/{id}`
- 方法：GET
- 描述：获取退款详细信息
- 请求头：`Authorization: Bearer {token}`
- 响应结果：同退款列表中的单个记录

### 3.4 取消退款
- 路径：`/api/v1/orders/refunds/{id}/cancel`
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

### 3.5 提交退货物流
- 路径：`/api/v1/orders/refunds/{id}/logistics`
- 方法：POST
- 描述：提交退货物流信息
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "company": "string",    // 物流公司，必填
    "trackingNo": "string"  // 物流单号，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "提交成功",
    "data": null
}
```

## 4. 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 3001 | 商品不存在 | 检查商品ID |
| 3002 | 商品已下架 | 等待商品上架 |
| 3003 | 库存不足 | 减少购买数量 |
| 3004 | 地址不存在 | 检查地址ID |
| 3005 | 优惠券无效 | 检查优惠券 |
| 3006 | 订单不存在 | 检查订单ID |
| 3007 | 订单状态错误 | 检查订单状态 |
| 3008 | 支付超时 | 重新下单 |
| 3009 | 退款申请已存在 | 检查退款状态 |
| 3010 | 退款金额超限 | 检查退款金额 |

## 5. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 