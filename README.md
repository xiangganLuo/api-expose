<p align="center">
  <img src="statics/imgs/logo.svg" alt="API Expose Logo" width="120"/>
</p>
<h1 align="center">APEx</h1>
<p align="center">
  <strong>围绕「apex-core」的 API 资产内核与扩展生态</strong>
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
  <strong>APEx (API Expose)</strong> 是一套围绕 <code>apex-core</code> 打造的 API 资产托管与治理内核，其他模块均作为围绕内核的能力扩展与工程化辅助。
</p>

---

## 📚 项目愿景

- 以 <code>apex-core</code> 为稳定内核：统一领域模型、仓储端口、触发器边界与基础设施抽象。
- 其他模块（如 <code>apex-infra</code>、<code>apex-framework</code>、<code>apex-server</code>、<code>apex-system</code>、<code>frontend</code>）均围绕 core 提供运行支撑、工程脚手架、治理能力与可视化。
- 坚持“解耦优先、演进友好、生态开放”，让 API 资产在不同业务形态下平滑扩展与复用。

---

## 🏗 技术架构

<strong>apex-core</strong> 采用 DDD 分层，包含以下子模块：

- <code>api-expose-api</code>：外部契约与公共类型（DTO/VO/错误码）。
- <code>api-expose-domain</code>：领域层（实体、值对象、领域服务、仓储接口）。
- <code>api-expose-infrastructure</code>：基础设施（DAO/PO、外部系统适配器、缓存/HTTP 等）。
- <code>api-expose-trigger</code>：触发层（REST/Job/WebSocket 等入口，对外暴露用例）。

> 说明：<code>apex-core</code> 是全仓库的设计中心，其他模块仅提供可选扩展，不改变核心模型和约束。

### 依赖与版本基线（参考 apex-dependencies）

本项目通过 <code>apex-dependencies</code> 统一管理依赖版本（BOM），确保全仓库一致性与安全性。典型基线：

- Spring Boot 2.7.x / Spring Framework 5.3.x
- MyBatis-Plus 3.5.x、Dynamic-Datasource 4.3.x
- Redisson 3.41.x、Netty 4.1.x
- MapStruct、Lombok、Guava 等工程工具

> 详细版本以 <code>apex-dependencies/pom.xml</code> 为准。

---

## 🌟 能力概览

- 高性能非阻塞转发与网关路由（基于 Reactor/Netty 的栈）。
- 统一鉴权与多租户语义（仓储接口 + 基础设施解耦）。
- 动态治理（限流、熔断、访问控制）与调用审计。
- 统计计量与（预留）计费结算能力。

---

## 📂 仓库模块说明

- <code>apex-core</code>：内核与领域实现，整库讲解的中心点。
- <code>apex-dependencies</code>：依赖版本 BOM（平台基线）。
- <code>apex-framework</code>：通用框架增强与 Spring Boot Starters。
- <code>apex-infra</code>：基础设施扩展与通用中间件集成（DB、文件、任务、监控等）。
- <code>apex-server</code>：服务装配与可运行形态（组合 core 与框架/基础设施）。
- <code>apex-system</code>：系统层通用能力（用户、权限、审计等）。
- <code>frontend</code>：可视化管理与运营看板。

项目根目录概要：

```text
.
├── apex-core/                # 核心：api-expose-* 子模块
├── apex-dependencies/        # 统一依赖版本（BOM）
├── apex-framework/           # 框架增强 & Starters
├── apex-infra/               # 基础设施扩展
├── apex-server/              # 可运行装配
├── apex-system/              # 通用系统能力
└── frontend/                 # 前端管理界面
```

---

## 🚧 快速启动（暂缓）

为聚焦 <code>apex-core</code> 的架构与演进，本阶段暂不提供「快速启动」指南。待内核接口与依赖基线稳定后，将补充包括：环境准备、数据库初始化、运行装配示例与前后端协同等内容。

---

## 📄 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证。

---

## 联系方式

如有问题或需要讨论内核设计，欢迎交流（备注 APEx）：

<p>
<img src="statics/img/weixin.png" alt="weixin" width="230px"/>
</p>
