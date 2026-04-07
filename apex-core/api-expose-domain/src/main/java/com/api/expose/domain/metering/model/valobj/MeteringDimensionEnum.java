package com.api.expose.domain.metering.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计量维度枚举值对象
 */
@Getter
@AllArgsConstructor
public enum MeteringDimensionEnum {

    BY_API("by_api", "按API统计"),
    BY_APP("by_app", "按应用统计"),
    BY_TENANT("by_tenant", "按租户统计");

    private final String code;
    private final String info;
}
