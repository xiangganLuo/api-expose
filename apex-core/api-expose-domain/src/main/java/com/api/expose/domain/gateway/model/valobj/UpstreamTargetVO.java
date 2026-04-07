package com.api.expose.domain.gateway.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 上游目标值对象 - 描述 API 真实后端地址
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpstreamTargetVO {

    /** 上游地址 (如 http://internal-service:8080) */
    private String targetUrl;
    /** 连接超时(毫秒) */
    private Integer connectTimeoutMs;
    /** 读取超时(毫秒) */
    private Integer readTimeoutMs;
    /** 是否启用 HTTPS */
    private Boolean httpsEnabled;
}
