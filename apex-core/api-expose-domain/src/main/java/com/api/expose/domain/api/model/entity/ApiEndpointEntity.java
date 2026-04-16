package com.api.expose.domain.api.model.entity;

import com.api.expose.domain.api.model.valobj.ApiDefinitionVO;
import com.api.expose.domain.api.model.valobj.HttpMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 端点实体 - 表示一个具体的 API 路由路径及 HTTP 方法
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiEndpointEntity {

    /** 端点ID */
    private Long endpointId;
    /** 所属资产ID */
    private Long assetId;
    /** 路由路径 (如 /api/v1/users) */
    private String path;
    /** HTTP 方法 */
    private HttpMethodEnum httpMethod;
    /** 端点名称 */
    private String name;
    /** 端点描述 */
    private String summary;
    /** API 定义（请求/响应模型） */
    private ApiDefinitionVO definition;
    /** 上游目标地址 */
    private String upstreamUrl;
    /** 超时时间(毫秒) */
    private Integer timeoutMs;
}
