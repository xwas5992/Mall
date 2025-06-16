# 开发文档

## 1. 开发环境

### 1.1 环境要求
1. 硬件要求
   - CPU：Intel i5/AMD Ryzen 5 及以上
   - 内存：16GB 及以上
   - 硬盘：256GB 及以上
   - 网络：100Mbps 及以上

2. 软件要求
   - 操作系统：Windows 10/11、macOS、Linux
   - JDK：OpenJDK 17
   - Maven：3.8.x
   - Git：2.x
   - Docker：20.x
   - Docker Compose：2.x
   - Node.js：18.x
   - npm：9.x
   - IDE：IntelliJ IDEA 2023.x
   - 数据库：MySQL 8.0、Redis 7.0
   - 中间件：Nacos、RocketMQ、Elasticsearch 8.x

### 1.2 环境搭建
1. 基础环境安装
   ```bash
   # 安装JDK
   # Windows
   choco install openjdk17
   # macOS
   brew install openjdk@17
   # Linux
   sudo apt install openjdk-17-jdk

   # 安装Maven
   # Windows
   choco install maven
   # macOS
   brew install maven
   # Linux
   sudo apt install maven

   # 安装Git
   # Windows
   choco install git
   # macOS
   brew install git
   # Linux
   sudo apt install git

   # 安装Docker
   # Windows/macOS
   # 下载Docker Desktop安装包
   # Linux
   curl -fsSL https://get.docker.com | sh

   # 安装Node.js
   # Windows
   choco install nodejs
   # macOS
   brew install node
   # Linux
   curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
   sudo apt install nodejs
   ```

2. 开发工具配置
   - IntelliJ IDEA
     - 安装插件：Lombok、Maven Helper、Git Toolbox、Docker
     - 配置JDK：File -> Project Structure -> SDKs
     - 配置Maven：File -> Settings -> Build Tools -> Maven
     - 配置Git：File -> Settings -> Version Control -> Git
     - 配置Docker：File -> Settings -> Build Tools -> Docker

3. 数据库环境
   ```bash
   # 使用Docker Compose启动数据库服务
   docker-compose up -d mysql redis nacos rocketmq elasticsearch
   ```

4. 项目配置
   ```bash
   # 克隆项目
   git clone https://github.com/your-org/mall.git
   cd mall

   # 安装依赖
   mvn clean install

   # 启动服务
   docker-compose up -d
   ```

## 2. 开发规范

### 2.1 代码规范
1. 命名规范
   - 类名：大驼峰命名法，如`UserService`
   - 方法名：小驼峰命名法，如`getUserById`
   - 变量名：小驼峰命名法，如`userName`
   - 常量名：全大写下划线分隔，如`MAX_RETRY_COUNT`
   - 包名：全小写点分隔，如`com.mall.user`
   - 文件名：与类名一致，如`UserService.java`

2. 注释规范
   - 类注释：说明类的用途、作者、创建时间
   - 方法注释：说明方法的功能、参数、返回值、异常
   - 变量注释：说明变量的用途
   - 关键代码注释：说明复杂逻辑的实现

3. 代码格式
   - 使用4个空格缩进
   - 行宽不超过120个字符
   - 方法之间空一行
   - 类之间空两行
   - 运算符前后加空格
   - 逗号后加空格

4. 异常处理
   - 使用自定义异常类
   - 异常信息要明确
   - 合理使用try-catch
   - 不要吞掉异常
   - 记录异常日志

### 2.2 项目规范
1. 项目结构
   ```
   mall
   ├── mall-common        # 公共模块
   ├── mall-gateway       # 网关服务
   ├── mall-auth         # 认证服务
   ├── mall-user         # 用户服务
   ├── mall-product      # 商品服务
   ├── mall-order        # 订单服务
   ├── mall-payment      # 支付服务
   ├── mall-system       # 系统服务
   ├── mall-message      # 消息服务
   ├── mall-file         # 文件服务
   ├── mall-api          # 接口模块
   ├── mall-admin        # 管理后台
   └── mall-app          # 移动应用
   ```

2. 模块结构
   ```
   mall-xxx
   ├── src
   │   ├── main
   │   │   ├── java
   │   │   │   └── com.mall.xxx
   │   │   │       ├── config        # 配置类
   │   │   │       ├── controller    # 控制器
   │   │   │       ├── service       # 服务层
   │   │   │       │   └── impl      # 服务实现
   │   │   │       ├── repository    # 数据访问层
   │   │   │       ├── model         # 数据模型
   │   │   │       │   ├── entity    # 实体类
   │   │   │       │   ├── dto       # 数据传输对象
   │   │   │       │   └── vo        # 视图对象
   │   │   │       ├── mapper        # MyBatis映射
   │   │   │       ├── util          # 工具类
   │   │   │       └── constant      # 常量类
   │   │   └── resources
   │   │       ├── mapper            # MyBatis XML
   │   │       ├── application.yml   # 应用配置
   │   │       └── bootstrap.yml     # 启动配置
   │   └── test                       # 测试代码
   └── pom.xml                        # 项目配置
   ```

3. 依赖管理
   - 统一管理依赖版本
   - 使用BOM管理依赖
   - 避免依赖冲突
   - 及时更新依赖版本

4. 配置管理
   - 使用配置中心
   - 区分环境配置
   - 敏感配置加密
   - 配置版本控制

### 2.3 开发流程
1. 需求分析
   - 理解业务需求
   - 确定技术方案
   - 评估开发周期
   - 制定开发计划

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
   - 安全测试

5. 部署上线
   - 代码合并
   - 环境部署
   - 功能验证
   - 监控告警

### 2.4 测试规范
1. 单元测试
   - 使用JUnit 5
   - 测试覆盖率>80%
   - 测试用例完整
   - 测试数据独立

2. 集成测试
   - 使用TestContainers
   - 模拟真实环境
   - 测试数据准备
   - 测试用例设计

3. 性能测试
   - 使用JMeter
   - 模拟并发场景
   - 性能指标监控
   - 性能瓶颈分析

4. 安全测试
   - 漏洞扫描
   - 渗透测试
   - 安全配置检查
   - 安全漏洞修复

## 3. 开发工具

### 3.1 IDE配置
1. IntelliJ IDEA
   - 安装插件
     - Lombok
     - Maven Helper
     - Git Toolbox
     - Docker
     - SonarLint
     - Alibaba Java Coding Guidelines
   - 配置代码模板
   - 配置代码检查
   - 配置快捷键

2. VS Code
   - 安装插件
     - Java Extension Pack
     - Spring Boot Extension Pack
     - Docker
     - GitLens
   - 配置代码模板
   - 配置代码检查
   - 配置快捷键

### 3.2 开发插件
1. 代码质量
   - SonarLint：代码质量检查
   - Alibaba Java Coding Guidelines：编码规范检查
   - CheckStyle-IDEA：代码风格检查
   - FindBugs-IDEA：Bug检查

2. 开发效率
   - Lombok：简化代码
   - Maven Helper：Maven依赖管理
   - Git Toolbox：Git工具集
   - Docker：容器管理
   - Rainbow Brackets：括号高亮
   - Key Promoter X：快捷键提示

3. 调试工具
   - Arthas：Java诊断工具
   - JProfiler：性能分析
   - VisualVM：JVM监控
   - Postman：接口测试

### 3.3 版本控制
1. Git规范
   - 分支管理
     - master：主分支
     - develop：开发分支
     - feature/*：功能分支
     - hotfix/*：修复分支
     - release/*：发布分支
   - 提交规范
     - feat：新功能
     - fix：修复bug
     - docs：文档更新
     - style：代码格式
     - refactor：重构
     - test：测试
     - chore：构建
   - 合并规范
     - 使用merge request
     - 代码审查
     - 冲突解决
     - 测试验证

2. 版本管理
   - 语义化版本
     - 主版本号：不兼容的API修改
     - 次版本号：向下兼容的功能性新增
     - 修订号：向下兼容的问题修正
   - 版本发布
     - 版本号更新
     - 更新日志
     - 发布说明
     - 部署文档

## 4. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 