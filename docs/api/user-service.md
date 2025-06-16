# 用户服务API文档

## 1. 用户认证

### 1.1 用户注册
- 路径：`/api/v1/users/register`
- 方法：POST
- 描述：新用户注册
- 请求参数：
```json
{
    "username": "string",     // 用户名，必填，长度6-20
    "password": "string",     // 密码，必填，长度8-20
    "phone": "string",        // 手机号，必填，11位
    "email": "string",        // 邮箱，选填
    "verifyCode": "string"    // 验证码，必填，6位
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "userId": "long",         // 用户ID
        "username": "string",     // 用户名
        "phone": "string",        // 手机号
        "email": "string",        // 邮箱
        "createdAt": "datetime"   // 注册时间
    }
}
```

### 1.2 用户登录
- 路径：`/api/v1/users/login`
- 方法：POST
- 描述：用户登录获取token
- 请求参数：
```json
{
    "username": "string",     // 用户名，必填
    "password": "string",     // 密码，必填
    "captcha": "string",      // 验证码，必填
    "captchaKey": "string"    // 验证码key，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "string",        // JWT token
        "refreshToken": "string", // 刷新token
        "expiresIn": 7200,        // 过期时间（秒）
        "userInfo": {
            "userId": "long",         // 用户ID
            "username": "string",     // 用户名
            "nickname": "string",     // 昵称
            "avatar": "string",       // 头像
            "phone": "string",        // 手机号
            "email": "string",        // 邮箱
            "roles": ["string"]       // 角色列表
        }
    }
}
```

### 1.3 刷新Token
- 路径：`/api/v1/users/refresh-token`
- 方法：POST
- 描述：刷新访问token
- 请求参数：
```json
{
    "refreshToken": "string"  // 刷新token，必填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "刷新成功",
    "data": {
        "token": "string",        // 新的JWT token
        "refreshToken": "string", // 新的刷新token
        "expiresIn": 7200         // 过期时间（秒）
    }
}
```

## 2. 用户信息

### 2.1 获取用户信息
- 路径：`/api/v1/users/info`
- 方法：GET
- 描述：获取当前登录用户信息
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": "long",         // 用户ID
        "username": "string",     // 用户名
        "nickname": "string",     // 昵称
        "avatar": "string",       // 头像
        "phone": "string",        // 手机号
        "email": "string",        // 邮箱
        "gender": "integer",      // 性别：0-未知，1-男，2-女
        "birthday": "date",       // 生日
        "roles": ["string"],      // 角色列表
        "permissions": ["string"], // 权限列表
        "createdAt": "datetime",  // 注册时间
        "updatedAt": "datetime"   // 更新时间
    }
}
```

### 2.2 更新用户信息
- 路径：`/api/v1/users/info`
- 方法：PUT
- 描述：更新用户基本信息
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "nickname": "string",     // 昵称，选填
    "avatar": "string",       // 头像，选填
    "gender": "integer",      // 性别，选填
    "birthday": "date",       // 生日，选填
    "email": "string"         // 邮箱，选填
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

### 2.3 修改密码
- 路径：`/api/v1/users/password`
- 方法：PUT
- 描述：修改用户密码
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "oldPassword": "string",  // 原密码，必填
    "newPassword": "string",  // 新密码，必填
    "confirmPassword": "string" // 确认密码，必填
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

## 3. 地址管理

### 3.1 获取地址列表
- 路径：`/api/v1/users/addresses`
- 方法：GET
- 描述：获取用户收货地址列表
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": [{
        "id": "long",             // 地址ID
        "receiver": "string",     // 收货人
        "phone": "string",        // 手机号
        "province": "string",     // 省份
        "city": "string",         // 城市
        "district": "string",     // 区县
        "detail": "string",       // 详细地址
        "isDefault": "boolean",   // 是否默认
        "createdAt": "datetime",  // 创建时间
        "updatedAt": "datetime"   // 更新时间
    }]
}
```

### 3.2 新增地址
- 路径：`/api/v1/users/addresses`
- 方法：POST
- 描述：新增收货地址
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "receiver": "string",     // 收货人，必填
    "phone": "string",        // 手机号，必填
    "province": "string",     // 省份，必填
    "city": "string",         // 城市，必填
    "district": "string",     // 区县，必填
    "detail": "string",       // 详细地址，必填
    "isDefault": "boolean"    // 是否默认，选填
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "新增成功",
    "data": {
        "id": "long"  // 地址ID
    }
}
```

### 3.3 修改地址
- 路径：`/api/v1/users/addresses/{id}`
- 方法：PUT
- 描述：修改收货地址
- 请求头：`Authorization: Bearer {token}`
- 请求参数：同新增地址
- 响应结果：
```json
{
    "code": 200,
    "message": "修改成功",
    "data": null
}
```

### 3.4 删除地址
- 路径：`/api/v1/users/addresses/{id}`
- 方法：DELETE
- 描述：删除收货地址
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

### 3.5 设置默认地址
- 路径：`/api/v1/users/addresses/{id}/default`
- 方法：PUT
- 描述：设置默认收货地址
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "设置成功",
    "data": null
}
```

## 4. 会员管理

### 4.1 获取会员信息
- 路径：`/api/v1/users/member`
- 方法：GET
- 描述：获取用户会员信息
- 请求头：`Authorization: Bearer {token}`
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "memberId": "long",           // 会员ID
        "level": "integer",           // 会员等级
        "levelName": "string",        // 等级名称
        "points": "integer",          // 积分
        "growth": "integer",          // 成长值
        "expireTime": "datetime",     // 过期时间
        "createdAt": "datetime",      // 开通时间
        "updatedAt": "datetime"       // 更新时间
    }
}
```

### 4.2 获取会员等级列表
- 路径：`/api/v1/users/member/levels`
- 方法：GET
- 描述：获取会员等级列表
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": [{
        "level": "integer",           // 等级
        "name": "string",             // 等级名称
        "icon": "string",             // 等级图标
        "growth": "integer",          // 所需成长值
        "privileges": ["string"],     // 特权列表
        "discount": "decimal",        // 折扣率
        "description": "string"       // 等级描述
    }]
}
```

### 4.3 获取积分明细
- 路径：`/api/v1/users/member/points/history`
- 方法：GET
- 描述：获取积分明细记录
- 请求头：`Authorization: Bearer {token}`
- 请求参数：
```json
{
    "page": "integer",    // 页码，从1开始
    "size": "integer",    // 每页大小
    "type": "string"      // 积分类型，选填
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
            "points": "integer",      // 积分变动
            "type": "string",         // 变动类型
            "description": "string",  // 变动描述
            "createdAt": "datetime"   // 创建时间
        }]
    }
}
```

## 5. 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 1001 | 用户名已存在 | 更换用户名 |
| 1002 | 手机号已注册 | 更换手机号 |
| 1003 | 验证码错误 | 重新获取验证码 |
| 1004 | 验证码已过期 | 重新获取验证码 |
| 1005 | 密码错误 | 检查密码 |
| 1006 | 账号已锁定 | 联系客服 |
| 1007 | 账号已禁用 | 联系客服 |
| 1008 | 地址数量超限 | 删除不常用地址 |
| 1009 | 会员已过期 | 续费会员 |
| 1010 | 积分不足 | 获取更多积分 |

## 6. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 