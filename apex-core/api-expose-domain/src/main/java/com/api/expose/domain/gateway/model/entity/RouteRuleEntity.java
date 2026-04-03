package com.api.expose.domain.gateway.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 路由规则领域实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteRuleEntity {

    private Long id;
    private Long apiAssetId;
    private Long endpointId;
    private String httpMethod;
    private String gatewayPath;
    private String upstreamPath;
    private String upstreamUrl;
    private String status;
    private Date createTime;
    private Date updateTime;
}
