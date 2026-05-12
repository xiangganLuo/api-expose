package com.api.expose.types.enums;

import com.api.expose.framework.common.exception.ErrorCode;

/**
 * core 错误码枚举类
 *
 * core 系统，使用 1-003-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 订阅模块 1-003-000-000 ==========
    ErrorCode SUBSCRIPTION_FORBIDDEN = new ErrorCode(1_003_000_001, "当前应用未订阅该 API 资产，或订阅尚未审核通过");

    // ========== 流量治理模块 1-003-001-000 ==========
    ErrorCode POLICY_ACCESS_DENIED = new ErrorCode(1_003_001_001, "已被访问控制策略拦截 (Forbidden)");
    ErrorCode POLICY_RATE_LIMITED = new ErrorCode(1_003_001_002, "请求过于频繁，触发限流 (Rate Limited)");
    ErrorCode POLICY_CIRCUIT_BROKEN = new ErrorCode(1_003_001_003, "服务触发熔断保护 (Circuit Broken)");
    // ========== 网关与前置校验 1-003-002-000 ==========
    ErrorCode API_KEY_INVALID = new ErrorCode(1_003_002_001, "无效或已禁用的 ApiKey");
    ErrorCode ROUTE_NOT_FOUND = new ErrorCode(1_003_002_002, "未找到匹配的路由规则");

    // ========== API 资产管理 1-003-003-000 ==========
    ErrorCode API_PARSE_FAILED = new ErrorCode(1_003_003_001, "无法解析 OpenAPI 内容，请检查格式是否正确");
    ErrorCode API_ENV_NOT_CONFIGURED = new ErrorCode(1_003_003_002, "所选环境未配置 BaseUrl，请先在环境配置中设置");
}
