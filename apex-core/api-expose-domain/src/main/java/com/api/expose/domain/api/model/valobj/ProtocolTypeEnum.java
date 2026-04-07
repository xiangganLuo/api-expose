package com.api.expose.domain.api.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 协议类型枚举值对象
 */
@Getter
@AllArgsConstructor
public enum ProtocolTypeEnum {

    HTTP("http", "HTTP协议"),
    HTTPS("https", "HTTPS协议");

    private final String code;
    private final String info;
}
