package com.api.expose.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 开发者应用 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperAppDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 应用ID */
    private Long appId;
    /** 租户ID */
    private String tenantId;
    /** 应用名称 */
    private String appName;
    /** 应用描述 */
    private String description;
    /** API Key */
    private String apiKey;
    /** 状态 */
    private String status;
    /** 回调地址 */
    private String callbackUrl;
}
