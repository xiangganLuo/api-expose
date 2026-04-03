package com.api.expose.domain.policy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 策略生效范围枚举值对象
 */
@Getter
@AllArgsConstructor
public enum PolicyScopeEnum {

    GLOBAL("global", "全局生效"),
    API_LEVEL("api_level", "API级别生效"),
    APP_LEVEL("app_level", "应用级别生效");

    private final String code;
    private final String info;
}
