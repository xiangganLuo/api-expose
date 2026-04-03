package com.api.expose.domain.app.model.entity;

import com.api.expose.domain.app.model.valobj.AppStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 开发者应用实体
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperAppEntity {

    /** 应用ID */
    private Long appId;
    /** 租户ID */
    private String tenantId;
    /** 应用名称 */
    private String appName;
    /** 应用描述 */
    private String description;
    /** 状态 */
    private AppStatusEnum status;
    /** API Key */
    private String apiKey;
    /** API Secret */
    private String apiSecret;
    /** 回调地址 */
    private String callbackUrl;

    public void activate() {
        this.status = AppStatusEnum.ACTIVE;
    }

    public void deactivate() {
        this.status = AppStatusEnum.INACTIVE;
    }
}
