package com.api.expose.domain.policy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证方式枚举值对象
 */
@Getter
@AllArgsConstructor
public enum AuthTypeEnum {

    API_KEY("api_key", "API Key 认证"),
    OAUTH2_CLIENT("oauth2_client", "OAuth2 客户端凭证模式"),
    NONE("none", "无认证");

    private final String code;
    private final String info;
}
