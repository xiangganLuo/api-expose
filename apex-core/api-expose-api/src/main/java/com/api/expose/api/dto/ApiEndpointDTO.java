package com.api.expose.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 端点响应 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiEndpointDTO {

    /** 路径 */
    private String path;
    /** HTTP方法 */
    private String httpMethod;
    /** 名称/概要 */
    private String summary;
    /** 上游地址 */
    private String upstreamUrl;
}
