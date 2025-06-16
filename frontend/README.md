# 前端项目说明

## 1. 项目结构

```
frontend
├── mall-admin          # 管理后台（Vue 3 + TypeScript + Element Plus）
│   ├── src
│   │   ├── api         # API接口
│   │   ├── assets      # 静态资源
│   │   ├── components  # 公共组件
│   │   ├── layouts     # 布局组件
│   │   ├── router      # 路由配置
│   │   ├── store       # 状态管理
│   │   ├── styles      # 样式文件
│   │   ├── utils       # 工具函数
│   │   └── views       # 页面组件
│   ├── public          # 公共资源
│   ├── tests           # 测试文件
│   ├── .env            # 环境变量
│   ├── package.json    # 项目配置
│   └── vite.config.ts  # Vite配置
│
└── mall-app            # 移动端应用（Flutter + Dart）
    ├── lib
    │   ├── api         # API接口
    │   ├── common      # 公共组件
    │   ├── config      # 配置文件
    │   ├── models      # 数据模型
    │   ├── pages       # 页面组件
    │   ├── services    # 服务层
    │   ├── utils       # 工具函数
    │   └── widgets     # 自定义组件
    ├── assets          # 静态资源
    ├── test            # 测试文件
    └── pubspec.yaml    # 项目配置
```

## 2. 技术栈

### 2.1 管理后台（mall-admin）
1. 核心框架
   - Vue 3：渐进式JavaScript框架
   - TypeScript：JavaScript的超集
   - Vite：下一代前端构建工具
   - Element Plus：基于Vue 3的组件库

2. 状态管理
   - Pinia：Vue 3的状态管理库
   - Vue Router：Vue.js的官方路由

3. 工具库
   - Axios：HTTP客户端
   - Lodash：实用工具库
   - Day.js：日期处理库
   - ECharts：图表库

4. 开发工具
   - ESLint：代码检查
   - Prettier：代码格式化
   - Husky：Git钩子
   - Jest：单元测试

### 2.2 移动端应用（mall-app）
1. 核心框架
   - Flutter：跨平台UI框架
   - Dart：编程语言
   - GetX：状态管理
   - Dio：HTTP客户端

2. 功能模块
   - 图片处理：photo_view
   - 下拉刷新：pull_to_refresh
   - 本地存储：shared_preferences
   - 网络状态：connectivity_plus

3. 开发工具
   - Flutter SDK
   - Android Studio
   - VS Code
   - Flutter DevTools

## 3. 开发规范

### 3.1 代码规范
1. 命名规范
   - 文件夹：小写字母，多个单词用连字符（-）连接
   - 组件文件：大驼峰命名法（PascalCase）
   - 工具文件：小驼峰命名法（camelCase）
   - 样式文件：小写字母，多个单词用连字符（-）连接

2. 注释规范
   - 文件头部注释：说明文件用途
   - 组件注释：说明组件功能
   - 方法注释：说明方法功能、参数、返回值
   - 复杂逻辑注释：说明实现思路

3. 代码格式
   - 使用ESLint + Prettier格式化代码
   - 使用EditorConfig统一编辑器配置
   - 遵循Vue 3风格指南
   - 遵循Flutter代码规范

### 3.2 项目规范
1. 目录结构
   - 按功能模块划分目录
   - 公共组件放在components目录
   - 工具函数放在utils目录
   - 类型定义放在types目录

2. 组件规范
   - 组件名使用大驼峰命名法
   - 组件属性使用小驼峰命名法
   - 组件事件使用kebab-case
   - 组件样式使用scoped

3. 状态管理
   - 使用Pinia管理全局状态
   - 使用GetX管理Flutter状态
   - 按模块划分store
   - 使用TypeScript类型

4. 接口规范
   - 统一接口请求封装
   - 统一错误处理
   - 统一响应格式
   - 使用TypeScript类型

## 4. 开发流程

### 4.1 环境搭建
1. 管理后台
   ```bash
   # 安装依赖
   cd mall-admin
   npm install

   # 启动开发服务器
   npm run dev

   # 构建生产版本
   npm run build
   ```

2. 移动端应用
   ```bash
   # 安装Flutter SDK
   # 配置环境变量
   # 运行Flutter doctor检查环境

   # 获取依赖
   cd mall-app
   flutter pub get

   # 运行应用
   flutter run
   ```

### 4.2 开发流程
1. 需求分析
   - 理解业务需求
   - 设计页面原型
   - 确定技术方案
   - 评估开发周期

2. 开发准备
   - 创建功能分支
   - 更新开发环境
   - 准备测试数据
   - 编写单元测试

3. 编码实现
   - 遵循编码规范
   - 编写单元测试
   - 代码审查
   - 持续集成

4. 测试验证
   - 单元测试
   - 集成测试
   - 性能测试
   - 兼容性测试

5. 部署上线
   - 代码合并
   - 环境部署
   - 功能验证
   - 监控告警

## 5. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------| 