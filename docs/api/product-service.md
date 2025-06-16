# 商品服务API文档

## 1. 商品管理

### 1.1 获取商品列表
- 路径：`/api/v1/products`
- 方法：GET
- 描述：获取商品列表，支持分页、筛选和排序
- 请求参数：
```json
{
    "page": "integer",        // 页码，从1开始
    "size": "integer",        // 每页大小
    "categoryId": "long",     // 分类ID，选填
    "keyword": "string",      // 搜索关键词，选填
    "minPrice": "decimal",    // 最低价格，选填
    "maxPrice": "decimal",    // 最高价格，选填
    "sort": "string",         // 排序字段，选填
    "order": "string",        // 排序方式：asc/desc，选填
    "status": "integer"       // 商品状态，选填
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
            "id": "long",             // 商品ID
            "name": "string",         // 商品名称
            "categoryId": "long",     // 分类ID
            "categoryName": "string", // 分类名称
            "brandId": "long",        // 品牌ID
            "brandName": "string",    // 品牌名称
            "price": "decimal",       // 销售价格
            "originalPrice": "decimal", // 原价
            "stock": "integer",       // 库存
            "sales": "integer",       // 销量
            "image": "string",        // 主图
            "images": ["string"],     // 图片列表
            "status": "integer",      // 状态：0-下架，1-上架
            "createdAt": "datetime",  // 创建时间
            "updatedAt": "datetime"   // 更新时间
        }]
    }
}
```

### 1.2 获取商品详情
- 路径：`/api/v1/products/{id}`
- 方法：GET
- 描述：获取商品详细信息
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "long",             // 商品ID
        "name": "string",         // 商品名称
        "categoryId": "long",     // 分类ID
        "categoryName": "string", // 分类名称
        "brandId": "long",        // 品牌ID
        "brandName": "string",    // 品牌名称
        "price": "decimal",       // 销售价格
        "originalPrice": "decimal", // 原价
        "stock": "integer",       // 库存
        "sales": "integer",       // 销量
        "image": "string",        // 主图
        "images": ["string"],     // 图片列表
        "detail": "string",       // 商品详情
        "specs": [{               // 规格列表
            "name": "string",     // 规格名称
            "values": ["string"]  // 规格值列表
        }],
        "skus": [{               // SKU列表
            "id": "long",        // SKU ID
            "specs": {           // 规格值
                "规格名": "规格值"
            },
            "price": "decimal",  // 价格
            "stock": "integer",  // 库存
            "code": "string"     // 商品编码
        }],
        "status": "integer",     // 状态
        "createdAt": "datetime", // 创建时间
        "updatedAt": "datetime"  // 更新时间
    }
}
```

### 1.3 新增商品
- 路径：`/api/v1/products`
- 方法：POST
- 描述：新增商品信息
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "name": "string",         // 商品名称，必填
    "categoryId": "long",     // 分类ID，必填
    "brandId": "long",        // 品牌ID，必填
    "price": "decimal",       // 销售价格，必填
    "originalPrice": "decimal", // 原价，必填
    "stock": "integer",       // 库存，必填
    "image": "string",        // 主图，必填
    "images": ["string"],     // 图片列表，必填
    "detail": "string",       // 商品详情，必填
    "specs": [{               // 规格列表，必填
        "name": "string",     // 规格名称
        "values": ["string"]  // 规格值列表
    }],
    "skus": [{               // SKU列表，必填
        "specs": {           // 规格值
            "规格名": "规格值"
        },
        "price": "decimal",  // 价格
        "stock": "integer",  // 库存
        "code": "string"     // 商品编码
    }]
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "新增成功",
    "data": {
        "id": "long"  // 商品ID
    }
}
```

### 1.4 修改商品
- 路径：`/api/v1/products/{id}`
- 方法：PUT
- 描述：修改商品信息
- 请求头：`Authorization: Bearer {token}`
- 请求参数：同新增商品
- 响应结果：
```json
{
    "code": 200,
    "message": "修改成功",
    "data": null
}
```

### 1.5 删除商品
- 路径：`/api/v1/products/{id}`
- 方法：DELETE
- 描述：删除商品
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

### 1.6 商品上下架
- 路径：`/api/v1/products/{id}/status`
- 方法：PUT
- 描述：修改商品上下架状态
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "status": "integer"  // 状态：0-下架，1-上架
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "修改成功",
    "data": null
}
```

## 2. 分类管理

### 2.1 获取分类列表
- 路径：`/api/v1/categories`
- 方法：GET
- 描述：获取商品分类列表
- 请求参数：
```json
{
    "parentId": "long"  // 父分类ID，选填，不传则获取一级分类
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": [{
        "id": "long",             // 分类ID
        "name": "string",         // 分类名称
        "parentId": "long",       // 父分类ID
        "level": "integer",       // 层级
        "sort": "integer",        // 排序
        "icon": "string",         // 图标
        "image": "string",        // 图片
        "status": "integer",      // 状态：0-禁用，1-启用
        "children": [{            // 子分类列表
            // 同父级结构
        }]
    }]
}
```

### 2.2 新增分类
- 路径：`/api/v1/categories`
- 方法：POST
- 描述：新增商品分类
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "name": "string",     // 分类名称，必填
    "parentId": "long",   // 父分类ID，选填
    "sort": "integer",    // 排序，选填
    "icon": "string",     // 图标，选填
    "image": "string"     // 图片，选填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "新增成功",
    "data": {
        "id": "long"  // 分类ID
    }
}
```

### 2.3 修改分类
- 路径：`/api/v1/categories/{id}`
- 方法：PUT
- 描述：修改商品分类
- 请求头：`Authorization: Bearer {token}`
- 请求参数：同新增分类
- 响应结果：
```json
{
    "code": 200,
    "message": "修改成功",
    "data": null
}
```

### 2.4 删除分类
- 路径：`/api/v1/categories/{id}`
- 方法：DELETE
- 描述：删除商品分类
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

## 3. 库存管理

### 3.1 获取库存记录
- 路径：`/api/v1/products/stock/history`
- 方法：GET
- 描述：获取商品库存变动记录
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "productId": "long",    // 商品ID，必填
    "skuId": "long",        // SKU ID，选填
    "page": "integer",      // 页码，从1开始
    "size": "integer",      // 每页大小
    "type": "string"        // 变动类型，选填
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
            "id": "long",             // 记录ID
            "productId": "long",      // 商品ID
            "skuId": "long",          // SKU ID
            "type": "string",         // 变动类型
            "quantity": "integer",    // 变动数量
            "before": "integer",      // 变动前数量
            "after": "integer",       // 变动后数量
            "operator": "string",     // 操作人
            "remark": "string",       // 备注
            "createdAt": "datetime"   // 创建时间
        }]
    }
}
```

### 3.2 调整库存
- 路径：`/api/v1/products/stock/adjust`
- 方法：POST
- 描述：调整商品库存
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "productId": "long",    // 商品ID，必填
    "skuId": "long",        // SKU ID，选填
    "quantity": "integer",  // 调整数量，必填
    "type": "string",       // 调整类型，必填
    "remark": "string"      // 备注，选填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "调整成功",
    "data": null
}
```

## 4. 商品评价

### 4.1 获取评价列表
- 路径：`/api/v1/products/{id}/reviews`
- 方法：GET
- 描述：获取商品评价列表
- 请求参数：
```json
{
    "page": "integer",      // 页码，从1开始
    "size": "integer",      // 每页大小
    "rating": "integer",    // 评分，选填
    "hasImage": "boolean",  // 是否有图片，选填
    "sort": "string"        // 排序方式，选填
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
            "id": "long",             // 评价ID
            "userId": "long",         // 用户ID
            "username": "string",     // 用户名
            "avatar": "string",       // 头像
            "rating": "integer",      // 评分
            "content": "string",      // 评价内容
            "images": ["string"],     // 图片列表
            "specs": {                // 商品规格
                "规格名": "规格值"
            },
            "createdAt": "datetime",  // 创建时间
            "replies": [{             // 回复列表
                "id": "long",         // 回复ID
                "content": "string",  // 回复内容
                "operator": "string", // 回复人
                "createdAt": "datetime" // 回复时间
            }]
        }]
    }
}
```

### 4.2 发表评价
- 路径：`/api/v1/products/reviews`
- 方法：POST
- 描述：发表商品评价
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "orderId": "long",      // 订单ID，必填
    "productId": "long",    // 商品ID，必填
    "skuId": "long",        // SKU ID，必填
    "rating": "integer",    // 评分，必填
    "content": "string",    // 评价内容，必填
    "images": ["string"]    // 图片列表，选填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "评价成功",
    "data": {
        "id": "long"  // 评价ID
    }
}
```

### 4.3 回复评价
- 路径：`/api/v1/products/reviews/{id}/reply`
- 方法：POST
- 描述：回复商品评价
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "content": "string"  // 回复内容，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "回复成功",
    "data": {
        "id": "long"  // 回复ID
    }
}
```

## 5. 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 2001 | 商品不存在 | 检查商品ID |
| 2002 | 商品已下架 | 等待商品上架 |
| 2003 | 库存不足 | 减少购买数量 |
| 2004 | 分类不存在 | 检查分类ID |
| 2005 | 分类下有商品 | 先删除商品 |
| 2006 | 评价已存在 | 检查订单状态 |
| 2007 | 评价已关闭 | 联系客服 |
| 2008 | 图片上传失败 | 重试上传 |
| 2009 | 规格值重复 | 修改规格值 |
| 2010 | SKU编码重复 | 修改SKU编码 |

## 6. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 