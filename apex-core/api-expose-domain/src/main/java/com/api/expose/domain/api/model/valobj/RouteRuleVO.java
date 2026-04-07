package com.api.expose.domain.api.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 路由规则值对象
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteRuleVO {

    /** API 资产 ID */
    private Long apiAssetId;
    /** 端点 ID */
    private Long endpointId;
    /** 请求方法 */
    private String httpMethod;
    /** 网关路径 */
    private String gatewayPath;
    /** 上游路径 */
    private String upstreamPath;
    /** 上游真实地址 */
    private String upstreamUrl;
    /** 状态 */
    private String status;
}
