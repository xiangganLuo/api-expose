<p align="center">
  <img src="statics/img/logo.svg" alt="API Expose Logo" width="120"/>
</p>
<h1 align="center">APEx</h1>
<p align="center">
  <strong>基于 DDD 架构的 API 开放平台</strong>
</p>
<p align="center">
    <a target="_blank" href="https://github.com/xiangganLuo/api-expose">
        <img src="https://img.shields.io/badge/APEx-v1.0.0--beta-blue.svg" />
    </a>
    <a target="_blank" href='https://www.apache.org/licenses/LICENSE-2.0.html'>
        <img src='https://img.shields.io/badge/license-Apache%202.0-green.svg'/>
    </a>
    <a target="_blank" href="https://github.com/xiangganLuo/api-expose">
        <img src="https://img.shields.io/github/stars/xiangganLuo/api-expose.svg?style=social" alt="github star"/>
    </a>
</p>
<p align="center">
  <strong>APEx (API Expose)</strong> 是一个面向企业的 API 开放平台解决方案，采用领域驱动设计（DDD）架构，提供从 API 资产定义、网关路由、流量治理到监控计量的全生命周期管理能力。
</p>

---

## 🌟 核心特性

- 🏗️ **DDD 架构设计** - 清晰的领域边界与分层架构
- 📦 **API 资产管理** - 完整的 API 资产定义与版本管理
- 🚀 **网关代理** - 高性能的异步 HTTP 转发
- 🛡️ **流量治理** - 限流、熔断、访问控制
- 📊 **监控计量** - 实时调用统计与趋势分析
- ✅ **订阅审批** - 应用订阅与权限管控
- 💻 **现代化前端** - Vue3 + Element Plus 管理后台

---

## 📚 项目愿景

- 以 <code>apex-core</code> 为稳定内核：统一领域模型、仓储端口、触发器边界与基础设施抽象。
- 其他模块（如 <code>apex-infra</code>、<code>apex-framework</code>、<code>apex-server</code>、<code>apex-system</code>、<code>frontend</code>）均围绕 core 提供运行支撑、工程脚手架、治理能力与可视化。
- 坚持“解耦优先、演进友好、生态开放”，让 API 资产在不同业务形态下平滑扩展与复用。

---

## 🛠️ 技术栈

### 后端

- **核心框架**: Spring Boot 2.7.x
- **持久层**: MyBatis Plus 3.5.x
- **数据库**: MySQL 8.0+
- **缓存**: Redis (可选)
- **响应式**: WebFlux (网关转发)
- **工具库**: Lombok, Hutool, MapStruct

### 前端

- **框架**: Vue 3 + TypeScript
- **UI 组件**: Element Plus
- **构建工具**: Vite
- **图表**: ECharts
- **状态管理**: Pinia

---

## 🚀 快速开始

### 环境要求

- JDK 8+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis (可选)

### 后端启动

1. **创建数据库并执行 SQL 脚本**:

```bash
mysql -u root -p < sql/mysql/init.sql
```

2. **修改配置文件** `apex-server/src/main/resources/application-dev.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/apex?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

3. **启动后端服务**:

```bash
# 方式1: Maven 插件启动
mvn -pl apex-server -am spring-boot:run

# 方式2: 打包后启动
mvn -T 1C -DskipTests package
java -jar apex-server/target/apex-server.jar
```

### 前端启动

```bash
cd frontend/admin
npm install
npm run dev
```

访问 http://localhost:5173 即可进入管理后台。

---

## 📋 能力概览

### 1. API 资产管理

- 🔧 **资产定义与版本管理** - 支持 API 资产的创建、编辑、版本控制
- 🌍 **多环境配置** - 开发/测试/生产环境隔离，每个环境独立配置上游服务
- 🔗 **上游服务配置** - 灵活的上游 URL、超时、重试等参数配置
- 📍 **端点路径管理** - RESTful 风格的端点定义，支持 GET/POST/PUT/DELETE 等方法
- 📄 **资产详情页** - 统一的资产详情视图，集成环境、上游、端点管理

### 2. 网关路由与代理

- 🎯 **动态路由规则** - 基于路径和 HTTP 方法的智能路由匹配
- 📤 **请求参数透传** - 完整支持 Query Parameters、Headers、Body 的透传
- ⚡ **异步 HTTP 转发** - 基于 WebFlux 的高性能异步转发
- 🔐 **订阅鉴权** - 网关层强制校验应用对 API 资产的订阅状态
- 📝 **调用流水记录** - 自动记录每次调用的路径、状态码、延迟等信息

### 3. 应用管理

- 👤 **开发者应用注册** - 支持应用的创建、编辑、删除
- 🔑 **API Key 生成** - 自动生成唯一的 API Key 用于身份认证
- 🎛️ **应用状态控制** - ACTIVE/INACTIVE 状态管理
- 📱 **应用详情页** - 以应用为中心的详情视图，集成订阅管理

### 4. 订阅管理

- 📋 **应用订阅 API 资产** - 应用可申请订阅特定的 API 资产
- ✅ **订阅审批流程** - PENDING -> APPROVED/REJECTED 的审批流转
- 🔄 **订阅状态管理** - 支持订阅的启用、禁用、撤销
- 🔍 **智能资产选择** - 通过远程搜索下拉框快速选择 API 资产
- 🛡️ **订阅权限校验** - 网关层强制校验订阅状态，未订阅或未审批的请求将被拦截

### 5. 流量治理

- 🔌 **策略扩展点 (SPI)** - 定义了标准的限流、熔断、访问控制扩展接口
- 🚦 **限流策略** - 支持 QPS 限流，可按应用、资产、全局维度配置
- 🔥 **熔断降级** - 支持基于错误率和响应时间的熔断保护
- 🚫 **访问控制** - 支持 IP 黑白名单过滤
- 📐 **策略范围** - 支持 GLOBAL/API_LEVEL/APP_LEVEL 三种策略作用域
- ⛓️ **动态策略链** - 网关层按优先级动态执行策略链 (访问控制 -> 限流 -> 熔断)
- 💾 **默认实现** - 提供基于内存的默认限流实现，可平滑替换为 Sentinel

### 6. 监控计量

- 📊 **调用流水记录** - 自动记录每次 API 调用的详细信息
- 📈 **全局流量趋势** - 近 N 天的总调用量趋势图
- 👥 **应用维度指标** - 按应用统计总调用、成功率、失败率、平均延迟
- 📦 **资产维度指标** - 按 API 资产统计调用指标
- 📉 **多系列图表** - 同时展示调用量、成功/失败次数、延迟趋势
- 🎴 **概览统计卡片** - 直观展示关键指标的汇总数据
- 🔎 **智能筛选** - 支持按维度(全局/应用/资产)、时间范围动态查询

---

## 📁 项目结构

```
api-expose/
├── apex-dependencies/          # 依赖管理
├── apex-framework/             # 框架扩展
├── apex-infra/                 # 基础设施
├── apex-system/                # 系统模块
├── apex-core/                  # 核心业务
│   ├── api-expose-domain/     # 领域层
│   ├── api-expose-infrastructure/ # 基础设施层
│   ├── api-expose-trigger/    # 触发器层
│   └── api-expose-types/      # 类型定义
├── apex-server/                # 服务启动
├── frontend/                   # 前端项目
│   └── admin/                  # 管理后台
└── sql/                        # 数据库脚本
```

---

## 🎯 核心领域

### API 领域 (api)

- API 资产管理
- 环境配置
- 上游服务
- 端点定义
- 路由规则

### 应用领域 (app)

- 开发者应用
- 订阅关系
- 权限管控

### 网关领域 (gateway)

- 路由匹配
- 请求转发
- 鉴权验证

### 策略领域 (policy)

- 限流规则
- 熔断规则
- 访问控制

### 计量领域 (metering)

- 调用记录
- 统计分析

---

## 📄 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证。

---

## 联系方式

如有问题或需要讨论内核设计，欢迎交流（备注 APEx）：

<p>
<img src="statics/img/weixin.png" alt="weixin" width="230px"/>
</p>
