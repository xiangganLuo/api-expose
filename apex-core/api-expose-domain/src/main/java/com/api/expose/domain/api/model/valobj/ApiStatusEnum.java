package com.api.expose.domain.api.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 资产状态枚举值对象
 */
@Getter
@AllArgsConstructor
public enum ApiStatusEnum {

    DRAFT("draft", "草稿"),
    PUBLISHED("published", "已发布"),
    DEPRECATED("deprecated", "已废弃"),
    OFFLINE("offline", "已下架");

    private final String code;
    private final String info;

    public static ApiStatusEnum getEnumByCode(String code) {
        for (ApiStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return DRAFT;
    }
}
