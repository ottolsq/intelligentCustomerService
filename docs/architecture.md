# 智能客服 SaaS 平台 - 微服务架构设计

## 一、项目概述

为电商平台提供智能客服服务的 SaaS 多租户平台，采用混合模式（规则+AI）为用户自动解答疑问。

**技术选型**：Java 21 + Spring Boot 3.5.7 + Spring Cloud 2024.0.0 + Spring Cloud Alibaba 2023.0.3.3

---

## 二、架构总览

```
┌─────────────────────────────────────────────────────────────────────┐
│                            客户端层                                   │
│  管理后台  │  租户门户  │  终端用户聊天  │  第三方平台  │  移动端SDK   │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────────┐
│                    API Gateway (端口 8080)                           │
│  路由转发 │ JWT验证 │ 租户识别 │ 限流 │ CORS                        │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
    ┌──────────────────────────┼──────────────────────────┐
    │                          │                          │
┌───▼──────────┐  ┌───────────▼──────────┐  ┌───────────▼──────────┐
│ ics-platform │  │ ics-conversation     │  │ ics-knowledge        │
│ 认证+租户+用户│  │ 会话管理+WebSocket   │  │ FAQ+文档+向量检索     │
│ 端口 8101    │  │ 端口 8102            │  │ 端口 8103            │
└──────────────┘  └──────────────────────┘  └──────────────────────┘

┌───────────────┐  ┌──────────────────────┐  ┌──────────────────────┐
│ ics-rule-     │  │ ics-ai               │  │ ics-analytics        │
│ engine        │  │ LLM集成+意图识别      │  │ 数据分析+看板         │
│ 端口 8104     │  │ 端口 8105            │  │ 端口 8106            │
└───────────────┘  └──────────────────────┘  └──────────────────────┘

┌──────────────────────┐
│ ics-integration      │
│ 平台适配器(淘宝/微信) │
│ 端口 8107            │
└──────────────────────┘

┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  MySQL 8     │  │  Redis 7     │  │  RabbitMQ    │  │  Nacos 2.x   │
│  各服务逻辑库 │  │  缓存/会话   │  │  异步事件    │  │  注册/配置   │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
```

---

## 三、服务划分（8个服务）

| 服务 | 端口 | 职责 |
|------|------|------|
| **ics-gateway** | 8080 | API网关：路由、认证、租户识别、限流 |
| **ics-platform** | 8101 | 认证 + 租户管理 + 用户权限（合并减少运维） |
| **ics-conversation** | 8102 | 会话管理、消息记录、WebSocket实时通信 |
| **ics-knowledge** | 8103 | FAQ管理、文档管理、向量语义搜索 |
| **ics-rule-engine** | 8104 | 自动回复规则、关键词匹配、LiteFlow规则引擎 |
| **ics-ai** | 8105 | LLM集成（Spring AI）、意图分类、提示词模板 |
| **ics-analytics** | 8106 | 数据统计分析、看板、报表 |
| **ics-integration** | 8107 | 外部平台适配器（淘宝、微信、网页Widget） |

---

## 四、多租户策略

**方案：共享数据库 + tenant_id 列隔离**

- 所有表包含 `tenant_id` 字段
- 通过 MyBatis-Plus `TenantLineInnerInterceptor` 自动附加 `WHERE tenant_id = ?`
- 租户上下文通过 JWT → Gateway 提取 → ThreadLocal 传递
- 企业大客户可升级为独立 Schema 隔离

### 租户上下文流转

```
请求到达 Gateway
    │
    ▼
Gateway 从以下方式识别租户：
  1. JWT claim (x-tenant-id)
  2. API Key 查询 (X-API-Key header → tenant)
  3. 子域名 (tenant1.app.com → tenant)
    │
    ▼
Gateway 注入 Header: X-Tenant-Id: 12345
    │
    ▼
下游服务通过：
  - ThreadLocal TenantContext 读取租户ID
  - MyBatis 拦截器自动附加 WHERE tenant_id = ?
  - Feign 拦截器在微服务调用间传播租户上下文
```

---

## 五、Maven 多模块结构

```
intelligentCustomerService/ics/
├── pom.xml                          # 父POM，统一依赖管理 + 仓库配置
├── ics-common/                      # 公共模块
│   ├── pom.xml
│   ├── ics-common-core/             # 工具类、Result<T>、全局异常
│   ├── ics-common-security/         # JWT、TenantContext、SecurityUtils
│   ├── ics-common-mybatis/          # MyBatis配置、租户拦截器、BaseEntity
│   ├── ics-common-redis/            # Redis配置、分布式锁
│   ├── ics-common-rabbitmq/         # RabbitMQ配置、事件基类
│   ├── ics-common-log/              # 日志、链路追踪
│   └── ics-common-swagger/          # OpenAPI/Swagger配置
├── ics-services/
│   ├── pom.xml
│   ├── ics-gateway/                 # API网关
│   ├── ics-platform/                # 认证+租户+用户
│   ├── ics-conversation/            # 会话服务
│   ├── ics-knowledge/               # 知识库服务
│   ├── ics-rule-engine/             # 规则引擎
│   ├── ics-ai/                      # AI服务
│   ├── ics-analytics/               # 分析服务
│   └── ics-integration/             # 集成服务
├── ics-deploy/                      # Docker Compose、部署脚本（待创建）
│   └── docker-compose.yml
├── sql/                             # 数据库脚本（待创建）
│   └── init/
└── docs/                            # 文档
    └── architecture.md
```

---

## 六、技术栈清单

| 层面 | 技术 |
|------|------|
| 语言 | Java 21 |
| 框架 | Spring Boot 3.5.7 |
| 微服务 | Spring Cloud 2024.0.0 + Spring Cloud Alibaba 2023.0.3.3 |
| 注册/配置中心 | Nacos 2.x |
| API网关 | Spring Cloud Gateway |
| RPC | OpenFeign + LoadBalancer |
| 熔断 | Resilience4j |
| ORM | MyBatis-Plus 3.5.9 |
| 多租户 | MyBatis-Plus TenantLineInnerInterceptor |
| 数据库 | MySQL 8.0（每服务逻辑库） |
| 缓存 | Redis 7 + Redisson 3.41.0 |
| 消息队列 | RabbitMQ |
| 认证 | Spring Security + JWT 0.12.6 |
| LLM集成 | Spring AI 1.0.0-M6 |
| 规则引擎 | LiteFlow 2.13.0 |
| 向量搜索 | Redis Stack |
| 文件存储 | MinIO |
| API文档 | SpringDoc OpenAPI 2.8.4 |
| 构建 | Maven |
| 仓库 | 阿里云镜像 + Maven Central |

---

## 七、数据库设计

### 每服务数据库分配

| 服务 | 主数据库 | 其他存储 |
|------|---------|---------|
| ics-platform | MySQL | Redis（会话、JWT黑名单） |
| ics-conversation | MySQL | Redis（活跃会话）、RabbitMQ（消息事件） |
| ics-knowledge | MySQL | Redis（热FAQ缓存、向量存储） |
| ics-rule-engine | MySQL | Redis（规则缓存） |
| ics-ai | MySQL | Redis（响应缓存） |
| ics-analytics | MySQL | RabbitMQ（事件消费） |
| ics-integration | MySQL | RabbitMQ（Webhook事件） |

**核心原则**：每个服务拥有自己的数据库，**禁止跨服务直接数据库访问**，通信通过 Feign（同步）或 RabbitMQ（异步）。

---

## 八、核心业务流程

### 用户消息 → 智能回复

```
用户发送消息
    │
    ▼
[1] 规则引擎匹配（关键词、时间规则）
    │
    ├── 匹配成功 → 返回规则回复
    │
    └── 未匹配 → [2] AI服务意图分类
                     │
                     ├── 已知意图 → [3] 知识库RAG检索 → 返回FAQ答案
                     │
                     └── 未知意图 → [4] LLM生成回复
                                      │
                                      ▼
                                   [5] 置信度检查
                                      ≥0.8 → 返回AI回复
                                      <0.8  → 转人工
```

### RabbitMQ 主题

| 主题 | 生产者 | 消费者 |
|------|--------|--------|
| `conversation.message.received` | conversation-svc | rule-engine, ai-service, analytics |
| `conversation.ai.response` | ai-service | conversation-svc, analytics |
| `conversation.agent.escalation` | rule-engine/ai | conversation-svc, notification |
| `tenant.billing.event` | 各服务 | tenant-service, analytics |

---

## 九、安全设计

### JWT Token 结构

```json
{
  "sub": "user-123",
  "tenantId": "tenant-456",
  "userId": "user-123",
  "roles": ["admin", "agent"],
  "permissions": ["conversation:read", "knowledge:write"],
  "dataScope": "tenant",
  "exp": 1719878400,
  "iat": 1719792000
}
```

### API Key 流程（平台集成）

```
集成平台
    │
    │  POST /api/v1/messages
    │  Headers:
    │    X-API-Key: ak_xxxxx
    │    X-Tenant-Id: tenant-456
    │
    ▼
Gateway 验证：
  1. X-API-Key 存在于 Redis 中
  2. Key 有效且未过期
  3. Key 属于 X-Tenant-Id
  4. Key 有所需权限
    │
    ▼ (验证通过)
路由到 integration-service
```

### 限流策略

按租户在 Gateway 层限流（基于 Redis）：

| 套餐 | 速率限制 |
|------|---------|
| 免费版 | 10 req/s，突发 20 |
| 专业版 | 500 req/s，突发 1000 |
| 企业版 | 2000 req/s，突发 5000 |

---

## 十、关键设计决策

### 1. Spring AI 做 LLM 集成

**优点**：避免供应商锁定，切换 LLM 提供商只需改配置
**缺点**：Spring AI 仍在成熟期，API 可能变化

### 2. MyBatis-Plus + 租户拦截器

**优点**：零代码修改实现租户过滤，降低忘记 `WHERE tenant_id = ?` 的风险
**缺点**：所有查询经过拦截器，需要处理跨租户查询的特殊情况

### 3. LiteFlow 代替 Drools

**优点**：轻量级、易学、支持规则热加载
**理由**：自动回复规则不需要 Drools 的完整功能

### 4. Redis Stack 做向量搜索

**优点**：无需新增基础设施
**缺点**：内存昂贵，数据量大时需要迁移到 Milvus

### 5. RabbitMQ 起步，后期可迁移 Kafka

**理由**：独立开发者初期消息量不需要 Kafka 的吞吐量，RabbitMQ 部署运维更简单

### 6. Monorepo

**理由**：单一构建、统一依赖管理、适合独立开发者

---

## 十一、分阶段实施计划

| 阶段 | 内容 | 预估天数 | 交付物 |
|------|------|---------|--------|
| **Phase 0** | 项目骨架、公共模块、Docker基础设施 | 5天 | 可编译的Monorepo + Nacos/MySQL/Redis/RabbitMQ运行 |
| **Phase 1** | 平台服务：认证、租户、用户、RBAC | 8天 | 登录、JWT、租户CRUD、租户数据隔离 |
| **Phase 2** | API网关：路由、JWT验证、限流 | 4天 | 所有请求经8080网关转发 |
| **Phase 3** | 会话服务：会话管理、消息、WebSocket | 8天 | 创建会话、发消息、实时通信 |
| **Phase 4** | 知识库：FAQ管理、向量搜索 | 7天 | FAQ增删改查、语义搜索 |
| **Phase 5** | 规则引擎：自动回复规则 | 6天 | 关键词规则触发自动回复 |
| **Phase 6** | AI集成：LLM回复、意图分类 | 8天 | AI回复生成、提示词管理 |
| **Phase 7** | 数据分析+通知 | 5天 | 数据看板、事件通知 |
| **Phase 8** | 平台集成：淘宝/微信适配器 | 8天 | 外部平台消息接入 |

**总预估：约59天（约12周）**

---

## 十二、扩展路线图

### Phase 1: MVP（0-100租户）

- 所有服务，单MySQL实例（逻辑库）
- 单Redis实例
- RabbitMQ 单节点
- Nacos 单节点
- Docker Compose 单机部署
- 预期：每天 1K-5K 消息

### Phase 2: 增长期（100-1K租户）

- MySQL 读写分离
- Redis 集群
- Kafka 替换 RabbitMQ
- Nacos 集群（3节点）
- K8s 多机部署
- CDN 静态资源
- 预期：每天 10K-50K 消息

### Phase 3: 规模化（1K-10K租户）

- ClickHouse 分析数据库
- Milvus 向量搜索（独立部署）
- 企业租户独立 Schema
- 多区域部署
- Service Mesh（Istio）
- 预期：每天 100K+ 消息

---

## 十三、验证方式

每个阶段完成后的验证：

1. **Phase 0**: `mvn clean compile` 通过，`docker-compose up` 启动所有基础设施
2. **Phase 1**: 登录返回JWT，租户数据隔离，Swagger显示所有接口
3. **Phase 2**: 所有API经网关8080转发，无效JWT返回401，限流生效
4. **Phase 3**: WebSocket连接成功，消息收发正常，历史消息分页查询
5. **Phase 4**: FAQ管理正常，语义搜索返回相关结果
6. **Phase 5**: 规则配置生效，关键词匹配触发自动回复
7. **Phase 6**: LLM返回回复，意图分类准确，Token使用记录
8. **Phase 7**: 看板数据正确，通知按配置触发
9. **Phase 8**: 外部平台消息接入，回复正确路由回源
