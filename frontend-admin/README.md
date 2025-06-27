# 商城管理系统 - 管理员前端

## 概述

这是商城管理系统的管理员前端界面，提供了完整的后台管理功能，包括商品管理、用户管理、订单管理、分类管理等核心功能。

## 功能特性

### 🎯 核心功能
- **控制台仪表板** - 实时数据统计和系统状态监控
- **商品管理** - 商品的增删改查、库存管理
- **用户管理** - 用户信息查看、状态管理
- **订单管理** - 订单处理、状态更新
- **分类管理** - 商品分类的增删改查
- **统计报表** - 销售数据分析和图表展示
- **系统设置** - 系统配置和参数管理

### 🎨 界面特性
- **现代化设计** - 采用Bootstrap 5和现代化UI设计
- **响应式布局** - 支持桌面端和移动端访问
- **直观导航** - 侧边栏导航，操作便捷
- **实时反馈** - 操作状态实时提示
- **数据可视化** - 图表展示关键数据

## 文件结构

```
frontend-admin/
├── index.html          # 管理员主页面
├── login.html          # 管理员登录页面
├── css/
│   └── admin.css       # 管理员界面样式
├── js/
│   └── admin.js        # 管理员界面逻辑
└── README.md           # 说明文档
```

## 技术栈

- **HTML5** - 页面结构
- **CSS3** - 样式设计
- **JavaScript (ES6+)** - 交互逻辑
- **Bootstrap 5** - UI框架
- **Font Awesome** - 图标库
- **Chart.js** - 图表库

## 快速开始

### 1. 启动后端服务

确保以下微服务正在运行：

```bash
# 认证服务 (端口8081)
cd auth-service
mvn spring-boot:run

# 商品服务 (端口8082)
cd product-service
mvn spring-boot:run

# 用户服务 (端口8085)
cd user-service
mvn spring-boot:run
```

### 2. 启动前端服务

```bash
# 启动本地HTTP服务器
cd frontend-admin
python -m http.server 8080
# 或者使用Node.js
npx http-server -p 8080
```

### 3. 访问管理界面

打开浏览器访问：
- 登录页面：`http://localhost:8080/frontend-admin/login.html`
- 管理主页：`http://localhost:8080/frontend-admin/index.html`

## 功能详解

### 1. 管理员登录

- **安全认证** - JWT Token认证机制
- **角色验证** - 仅管理员用户可访问
- **会话管理** - 自动登录和会话保持
- **安全退出** - 清除本地存储的认证信息

### 2. 控制台仪表板

#### 数据统计卡片
- **总用户数** - 显示注册用户总数
- **商品总数** - 显示上架商品数量
- **订单总数** - 显示已完成订单数量
- **总收入** - 显示系统总收入金额

#### 最近订单
- 显示最新的订单信息
- 包含订单号、用户、金额、状态、时间
- 支持快速查看订单详情

#### 系统状态监控
- 实时监控各微服务状态
- 显示服务健康状态
- 异常状态自动标记

### 3. 商品管理

#### 商品列表
- 分页显示所有商品
- 支持按名称、分类、状态筛选
- 显示商品缩略图、价格、库存等信息

#### 商品操作
- **添加商品** - 通过模态框添加新商品
- **编辑商品** - 修改商品信息
- **删除商品** - 删除商品（需确认）
- **库存管理** - 更新商品库存数量

#### 商品信息字段
- 商品名称
- 商品价格
- 库存数量
- 商品分类
- 商品图片URL
- 商品描述

### 4. 用户管理

#### 用户列表
- 显示所有注册用户
- 包含用户ID、用户名、邮箱、角色等信息
- 显示用户注册时间和状态

#### 用户操作
- **查看用户** - 查看用户详细信息
- **状态管理** - 启用/禁用用户账户
- **删除用户** - 删除用户账户（需确认）

#### 用户角色
- **ADMIN** - 管理员用户
- **USER** - 普通用户

### 5. 订单管理

#### 订单列表
- 显示所有订单信息
- 包含订单号、用户、商品、金额、状态等
- 支持按状态筛选订单

#### 订单状态
- **PENDING** - 待处理
- **PROCESSING** - 处理中
- **COMPLETED** - 已完成
- **CANCELLED** - 已取消

#### 订单操作
- **查看详情** - 查看订单详细信息
- **状态更新** - 更新订单处理状态
- **订单处理** - 标记订单为已完成

### 6. 分类管理

#### 分类列表
- 显示所有商品分类
- 包含分类名称、描述、商品数量
- 支持分类的增删改查

#### 分类操作
- **添加分类** - 创建新的商品分类
- **编辑分类** - 修改分类信息
- **删除分类** - 删除分类（需确认）

### 7. 统计报表

#### 销售趋势图
- 显示销售数据趋势
- 支持按时间范围查看
- 图表化展示销售数据

#### 分类分布图
- 显示商品分类分布
- 饼图展示各分类占比
- 直观了解商品结构

### 8. 系统设置

#### 基本设置
- **商城名称** - 设置商城显示名称
- **管理员邮箱** - 设置管理员联系邮箱

#### 安全设置
- **会话超时** - 设置自动登出时间
- **操作日志** - 启用/禁用操作日志记录

## API接口

### 认证相关
- `POST /api/auth/login` - 管理员登录
- `GET /api/auth/verify` - 验证Token
- `GET /api/auth/users` - 获取用户列表
- `GET /api/auth/users/count` - 获取用户数量

### 商品相关
- `GET /api/products` - 获取商品列表
- `POST /api/products` - 添加商品
- `PUT /api/products/{id}` - 更新商品
- `DELETE /api/products/{id}` - 删除商品
- `GET /api/products/count` - 获取商品数量

### 订单相关
- `GET /api/products/orders` - 获取订单列表
- `GET /api/products/orders/recent` - 获取最近订单
- `PUT /api/products/orders/{id}/status` - 更新订单状态

### 分类相关
- `GET /api/products/categories` - 获取分类列表
- `POST /api/products/categories` - 添加分类
- `PUT /api/products/categories/{id}` - 更新分类
- `DELETE /api/products/categories/{id}` - 删除分类

## 安全特性

### 认证机制
- JWT Token认证
- 自动Token验证
- 会话超时处理
- 安全退出机制

### 权限控制
- 角色基础访问控制
- 管理员权限验证
- API接口权限检查

### 数据安全
- 输入数据验证
- XSS防护
- CSRF防护
- 敏感信息加密

## 浏览器兼容性

- Chrome 80+
- Firefox 75+
- Safari 13+
- Edge 80+

## 开发指南

### 添加新功能

1. **创建页面组件**
   ```javascript
   // 在admin.js中添加新页面逻辑
   loadNewPage() {
       // 页面加载逻辑
   }
   ```

2. **添加导航项**
   ```html
   <li class="nav-item">
       <a href="#newpage" class="nav-link" data-page="newpage">
           <i class="fas fa-icon"></i>
           <span>新功能</span>
       </a>
   </li>
   ```

3. **添加页面内容**
   ```html
   <div id="newpage" class="page-content">
       <!-- 页面内容 -->
   </div>
   ```

### 自定义样式

在`css/admin.css`中添加自定义样式：

```css
/* 自定义组件样式 */
.custom-component {
    /* 样式定义 */
}
```

### API集成

在`js/admin.js`中添加新的API调用：

```javascript
async loadNewData() {
    try {
        const response = await fetch(`${window.API_CONFIG.service.baseUrl}/endpoint`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                ...window.API_FETCH_CONFIG.headers
            }
        });
        // 处理响应数据
    } catch (error) {
        console.error('API调用失败:', error);
    }
}
```

## 故障排除

### 常见问题

1. **登录失败**
   - 检查后端服务是否正常运行
   - 验证用户名密码是否正确
   - 确认用户角色为ADMIN

2. **数据加载失败**
   - 检查网络连接
   - 验证API接口地址
   - 查看浏览器控制台错误信息

3. **页面显示异常**
   - 清除浏览器缓存
   - 检查CSS和JS文件路径
   - 验证Bootstrap和Font Awesome文件

### 调试技巧

1. **浏览器开发者工具**
   - 查看Console错误信息
   - 检查Network请求状态
   - 验证DOM元素结构

2. **API测试**
   - 使用Postman测试API接口
   - 验证请求参数和响应格式
   - 检查认证Token有效性

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础管理功能
- 完成用户界面设计

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

如有问题或建议，请联系开发团队。 