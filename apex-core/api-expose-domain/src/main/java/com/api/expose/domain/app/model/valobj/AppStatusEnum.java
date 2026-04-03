package com.api.expose.domain.app.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用状态枚举
 */
@Getter
@AllArgsConstructor
public enum AppStatusEnum {

    ACTIVE("active", "启用"),
    INACTIVE("inactive", "禁用");

    private final String code;
    private final String info;

    public static AppStatusEnum getEnumByCode(String code) {
        for (AppStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return INACTIVE;
    }
}
