package com.api.expose.domain.app.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订阅状态枚举
 */
@Getter
@AllArgsConstructor
public enum SubscriptionStatusEnum {

    PENDING("pending", "申请中"),
    APPROVED("approved", "已通过"),
    REJECTED("rejected", "已驳回"),
    REVOKED("revoked", "已撤销");

    private final String code;
    private final String info;

    public static SubscriptionStatusEnum getEnumByCode(String code) {
        for (SubscriptionStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return PENDING;
    }
}
