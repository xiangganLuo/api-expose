package com.api.expose.domain.gateway.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 路由状态枚举值对象
 */
@Getter
@AllArgsConstructor
public enum RouteStatusEnum {

    ACTIVE("active", "生效中"),
    INACTIVE("inactive", "未生效");

    private final String code;
    private final String info;
}
