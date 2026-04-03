package com.api.expose.domain.tenant.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租户状态枚举值对象
 */
@Getter
@AllArgsConstructor
public enum TenantStatusEnum {

    ACTIVE("active", "活跃"),
    SUSPENDED("suspended", "已停用"),
    EXPIRED("expired", "已过期");

    private final String code;
    private final String info;
}
