package com.api.expose.domain.tenant.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租户成员角色枚举值对象
 */
@Getter
@AllArgsConstructor
public enum MemberRoleEnum {

    ADMIN("admin", "管理员"),
    DEVELOPER("developer", "开发者"),
    VIEWER("viewer", "查看者");

    private final String code;
    private final String info;
}
