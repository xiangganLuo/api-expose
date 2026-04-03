package com.api.expose.domain.api.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP 方法枚举值对象
 */
@Getter
@AllArgsConstructor
public enum HttpMethodEnum {

    GET("GET", "查询"),
    POST("POST", "新增"),
    PUT("PUT", "修改"),
    DELETE("DELETE", "删除"),
    PATCH("PATCH", "部分修改");

    private final String code;
    private final String info;
}
