# Repository Guidelines

## 项目结构与模块
- 后端为 Maven 聚合工程：`apex-dependencies/`、`apex-framework/`、`apex-server/`、`apex-system/`、`apex-infra/`、`apex-core/`。
- 领域层：`apex-core/api-expose-domain/`；基础设施层：`apex-core/api-expose-infrastructure/`；通用基础设施：`apex-infra/`；框架扩展与 Starters：`apex-framework/`。
- 运行入口：`apex-server/src/main/java/com/api/expose/server/ServerApplication.java`。
- 前端：`frontend/admin/`（Vite + Vue3 + Element Plus）。
- 其他：SQL 脚本 `sql/mysql/`，静态资源 `statics/`。

## 构建、测试与本地运行
- 先决条件：JDK 8、Maven 3.8+、Node 18+、npm 9+。
- 后端打包：`mvn -T 1C -DskipTests package`。
- 本地运行后端：
  - `mvn -pl apex-server -am spring-boot:run`，或
  - `java -jar apex-server/target/apex-server.jar`。
- 前端开发：`cd frontend/admin && npm install && npm run dev`；生产构建：`npm run build:prod`。
- Docker（可选）：`docker build -t api-expose:local apex-server/`。

## 代码风格与命名
- Java：4 空格缩进；`package` 全小写；类 `UpperCamelCase`，方法/变量 `lowerCamelCase`。
- 类文件头必须包含作者签名：
- MapStruct 用于对象转换，Lombok 简化样板代码；避免在 `*Convert` 外扩散转换逻辑。
- 前端遵循 ESLint + Prettier + Stylelint（见 `frontend/admin/.eslintrc.js` 等）。
- controller不需要异常捕获，有统一的异常处理器。也不需要使用log.info打印日志

## 测试规范
- 测试框架：JUnit 5（`apex-spring-boot-starter-test` + Surefire）。
- 运行测试：`mvn -DskipTests=false test` 或模块定向 `mvn -pl apex-server test`。
- 命名：类以 `*Test` 结尾；聚焦领域服务与仓储的单元/集成测试。

## Commit 与 Pull Request
- 推荐 Conventional Commits，例如：`feat(core): add gateway route sync`。
- PR 需包含：变更说明、关联 Issue、风险与回滚、前后端截图/日志（如适用）。
- 合并前保证：`mvn -DskipTests package` 通过；前端 `pnpm build:prod` 通过；更新相关文档/SQL 迁移。

## 安全与配置
- 配置位于 `apex-server/src/main/resources/application-*.yaml`；敏感信息通过环境变量注入，勿提交真实密钥。
- 新增数据库变更请同步至 `sql/mysql/` 并在 README 标注执行顺序。

## Agent 专属说明
- 本文件为在仓库根目录生效的 AGENTS 指南；生成或修改代码时请保持模块边界清晰，不跨层耦合。
- 代码评审时重点关注：接口稳定性、事务与幂等、异常与日志规范、可观测性（日志/指标）。
