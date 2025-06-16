# 部署架构设计

## 1. 部署架构图

```
                                    Client
                                      │
                                      ▼
                                CDN + Nginx
                                      │
                                      ▼
                            Spring Cloud Gateway
                                      │
                    ┌─────────┬───────┴───────┬─────────┐
                    ▼         ▼               ▼         ▼
              User Service  Product    Order Service  Payment
                          Service                    Service
                    │         │               │         │
                    ▼         ▼               ▼         ▼
                MySQL     MySQL + ES      MySQL      MySQL
                    │         │               │         │
                    ▼         ▼               ▼         ▼
                  Redis     MinIO          RabbitMQ    Redis
```

## 2. 环境规划

### 2.1 开发环境（DEV）
- **服务器配置**
  - CPU：2核
  - 内存：4GB
  - 磁盘：50GB
  - 数量：1台

- **部署服务**
  - 所有微服务（单实例）
  - MySQL（单实例）
  - Redis（单实例）
  - RabbitMQ（单实例）
  - Nacos（单实例）

### 2.2 测试环境（TEST）
- **服务器配置**
  - CPU：4核
  - 内存：8GB
  - 磁盘：100GB
  - 数量：2台

- **部署服务**
  - 所有微服务（双实例）
  - MySQL（主从）
  - Redis（主从）
  - RabbitMQ（集群）
  - Nacos（集群）
  - ELK（单实例）

### 2.3 生产环境（PROD）
- **服务器配置**
  - CPU：8核
  - 内存：16GB
  - 磁盘：200GB
  - 数量：4台

- **部署服务**
  - 所有微服务（多实例）
  - MySQL（主从+读写分离）
  - Redis（集群）
  - RabbitMQ（集群）
  - Nacos（集群）
  - ELK（集群）
  - 监控系统（Prometheus + Grafana）

## 3. 容器化部署

### 3.1 Docker配置
```yaml
# 基础镜像
FROM openjdk:17-jdk-slim

# 工作目录
WORKDIR /app

# 复制JAR包
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java","-jar","app.jar"]
```

### 3.2 Kubernetes配置
```yaml
# 部署配置
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: mall/user-service:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "512Mi"
            cpu: "200m"
          limits:
            memory: "1Gi"
            cpu: "500m"
```

## 4. 网络配置

### 4.1 域名规划
- 开发环境：dev.mall.com
- 测试环境：test.mall.com
- 生产环境：www.mall.com

### 4.2 端口规划
| 服务名称 | 端口 | 说明 |
|---------|------|------|
| Gateway | 80/443 | 网关服务 |
| User Service | 8081 | 用户服务 |
| Product Service | 8082 | 商品服务 |
| Order Service | 8083 | 订单服务 |
| Payment Service | 8084 | 支付服务 |
| Nacos | 8848 | 服务注册中心 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| RabbitMQ | 5672 | 消息队列 |
| Elasticsearch | 9200 | 搜索引擎 |

### 4.3 负载均衡
- Nginx负载均衡
- Kubernetes Service
- Spring Cloud Gateway

## 5. 存储配置

### 5.1 数据库部署
- MySQL主从复制
- 读写分离
- 分库分表策略
- 备份策略

### 5.2 缓存部署
- Redis集群
- 主从复制
- 持久化配置
- 内存配置

### 5.3 文件存储
- MinIO集群
- 对象存储
- 图片处理
- 备份策略

## 6. 监控告警

### 6.1 系统监控
- Prometheus采集
- Grafana展示
- 告警规则
- 告警通知

### 6.2 应用监控
- Spring Boot Admin
- 健康检查
- 性能指标
- JVM监控

### 6.3 日志监控
- ELK Stack
- 日志收集
- 日志分析
- 日志告警

## 7. 安全配置

### 7.1 网络安全
- SSL证书
- 防火墙配置
- 安全组设置
- DDoS防护

### 7.2 应用安全
- 认证授权
- 数据加密
- 防SQL注入
- XSS防护

### 7.3 数据安全
- 数据备份
- 数据加密
- 访问控制
- 审计日志

## 8. 运维支持

### 8.1 部署流程
1. 代码提交
2. 自动构建
3. 自动测试
4. 自动部署
5. 健康检查
6. 灰度发布

### 8.2 运维工具
- Jenkins：持续集成
- GitLab：代码管理
- Jira：项目管理
- Confluence：文档管理

### 8.3 应急预案
- 服务降级
- 故障转移
- 数据恢复
- 回滚机制 