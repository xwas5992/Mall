# 技术栈说明

## 1. 后端技术栈

### 1.1 核心框架
- **Spring Boot 3.x**
  - 版本：3.2.x
  - 用途：基础框架
  - 特性：自动配置、内嵌服务器、生产级特性

- **Spring Cloud**
  - 版本：2023.x
  - 组件：
    - Spring Cloud Gateway：API网关
    - Spring Cloud OpenFeign：服务调用
    - Spring Cloud LoadBalancer：负载均衡
    - Spring Cloud Config：配置中心

### 1.2 数据存储
- **MySQL 8.x**
  - 用途：关系型数据库
  - 特性：事务支持、主从复制
  - 工具：MyBatis-Plus

- **Redis**
  - 版本：7.x
  - 用途：缓存、分布式锁
  - 特性：高性能、持久化、集群

- **Elasticsearch**
  - 版本：8.x
  - 用途：搜索引擎
  - 特性：全文检索、分布式

### 1.3 消息队列
- **RabbitMQ**
  - 版本：3.12.x
  - 用途：异步通信
  - 特性：可靠性、灵活的路由

### 1.4 服务治理
- **Nacos**
  - 版本：2.2.x
  - 用途：服务注册与发现、配置中心
  - 特性：动态服务发现、配置管理

- **Sentinel**
  - 版本：1.8.x
  - 用途：流量控制、熔断降级
  - 特性：实时监控、规则动态配置

### 1.5 监控运维
- **Spring Boot Admin**
  - 版本：3.2.x
  - 用途：应用监控
  - 特性：健康检查、指标监控

- **Prometheus + Grafana**
  - 用途：系统监控、可视化
  - 特性：时序数据、告警管理

- **SkyWalking**
  - 版本：9.x
  - 用途：分布式追踪
  - 特性：性能分析、依赖分析

### 1.6 安全框架
- **Spring Security**
  - 版本：6.x
  - 用途：认证授权
  - 特性：安全防护、OAuth2支持

- **JWT**
  - 用途：令牌管理
  - 特性：无状态、跨域支持

### 1.7 工具库
- **Lombok**
  - 用途：简化代码
  - 特性：注解处理器

- **MapStruct**
  - 用途：对象映射
  - 特性：编译时生成映射代码

- **Hutool**
  - 用途：工具集
  - 特性：常用工具类

## 2. 前端技术栈

### 2.1 核心框架
- **Vue 3**
  - 版本：3.3.x
  - 特性：组合式API、响应式系统

- **TypeScript**
  - 版本：5.x
  - 用途：类型系统
  - 特性：静态类型检查

### 2.2 状态管理
- **Pinia**
  - 版本：2.x
  - 用途：状态管理
  - 特性：类型安全、模块化

### 2.3 UI框架
- **Element Plus**
  - 版本：2.x
  - 用途：UI组件库
  - 特性：组件丰富、主题定制

### 2.4 构建工具
- **Vite**
  - 版本：5.x
  - 用途：构建工具
  - 特性：快速热更新、按需编译

### 2.5 工具库
- **Axios**
  - 版本：1.x
  - 用途：HTTP客户端
  - 特性：拦截器、请求取消

- **Vue Router**
  - 版本：4.x
  - 用途：路由管理
  - 特性：动态路由、导航守卫

## 3. 开发工具

### 3.1 IDE
- **IntelliJ IDEA**
  - 版本：2023.x
  - 用途：Java开发
  - 插件：
    - Lombok
    - Maven Helper
    - Spring Assistant

- **VS Code**
  - 版本：最新版
  - 用途：前端开发
  - 插件：
    - Volar
    - ESLint
    - Prettier

### 3.2 版本控制
- **Git**
  - 版本：2.x
  - 用途：代码版本控制
  - 工具：Git Flow

### 3.3 数据库工具
- **Navicat**
  - 用途：数据库管理
  - 特性：可视化操作

- **Redis Desktop Manager**
  - 用途：Redis管理
  - 特性：可视化操作

### 3.4 API工具
- **Postman**
  - 用途：API测试
  - 特性：接口调试、自动化测试

- **Swagger UI**
  - 用途：API文档
  - 特性：在线调试、文档生成

## 4. 部署环境

### 4.1 容器化
- **Docker**
  - 版本：24.x
  - 用途：容器化部署
  - 特性：轻量级、可移植

- **Kubernetes**
  - 版本：1.28.x
  - 用途：容器编排
  - 特性：自动扩缩容、服务发现

### 4.2 服务器
- **操作系统**：CentOS 8.x
- **Web服务器**：Nginx 1.24.x
- **JDK版本**：OpenJDK 17
- **Node版本**：18.x LTS

### 4.3 CI/CD
- **Jenkins**
  - 版本：2.x
  - 用途：持续集成
  - 特性：自动化构建、部署

- **GitLab CI**
  - 用途：持续集成
  - 特性：代码管理、CI/CD

## 5. 版本兼容性

### 5.1 浏览器支持
- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

### 5.2 移动端支持
- iOS >= 13
- Android >= 8.0

### 5.3 服务器要求
- CPU：4核+
- 内存：8GB+
- 磁盘：100GB+
- 网络：100Mbps+ 