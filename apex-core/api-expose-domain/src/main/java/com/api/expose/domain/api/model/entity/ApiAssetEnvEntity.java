package com.api.expose.domain.api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 资产环境配置实体
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiAssetEnvEntity {

    /** 主键ID */
    private Long id;
    /** 资产ID */
    private Long assetId;
    /** 租户ID */
    private Long tenantId;
    /** 环境标识 (如: test, prod) */
    private String envCode;
    /** 环境名称 (如: 测试环境) */
    private String envName;
    /** 后端基础路径 (如: http://api.test.com) */
    private String baseUrl;
    /** 状态 (0-开启, 1-关闭) */
    private Integer status;

    /**
     * 设置环境名称，若为空则取环境代码
     */
    public void validate() {
        if (envName == null || envName.trim().isEmpty()) {
            this.envName = envCode;
        }
    }
}
