package com.api.expose.domain.metering.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * API 调用记录领域实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiCallRecordEntity {

    private Long id;
    private Long tenantId;
    private Long apiAssetId;
    private Long appId;
    private String requestPath;
    private String httpMethod;
    private Integer responseCode;
    private Long latencyMs;
    private String callerIp;
    private Date callTime;

}
