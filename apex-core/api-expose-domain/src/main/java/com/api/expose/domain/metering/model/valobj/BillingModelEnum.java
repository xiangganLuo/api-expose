package com.api.expose.domain.metering.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计费模型枚举值对象
 */
@Getter
@AllArgsConstructor
public enum BillingModelEnum {

    FREE("free", "免费"),
    PREPAID("prepaid", "预付费套餐包"),
    POSTPAID("postpaid", "后付费按量计费");

    private final String code;
    private final String info;
}
