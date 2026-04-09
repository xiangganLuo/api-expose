package com.api.expose.domain.api.model.aggregate;

import com.api.expose.domain.api.model.entity.ApiEndpointEntity;
import com.api.expose.domain.api.model.entity.ApiVersionEntity;
import com.api.expose.domain.api.model.valobj.ApiStatusEnum;
import com.api.expose.domain.api.model.valobj.ProtocolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * API 资产聚合根 - 管理 API 的完整生命周期
 * 包含端点列表、版本管理、状态流转等核心业务行为
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiAssetAggregate {

    /** 资产ID */
    private Long assetId;
    /** 租户ID */
    private Long tenantId;
    /** API 名称 */
    private String name;
    /** API 描述 */
    private String description;
    /** API 分组 */
    private String groupName;
    /** 协议类型 */
    private ProtocolTypeEnum protocolType;
    /** API 状态 */
    private ApiStatusEnum status;
    /** 基础路径前缀 */
    private String basePath;
    /** 端点列表 */
    private List<ApiEndpointEntity> endpoints;
    /** 版本信息 */
    private List<ApiVersionEntity> versions;
    /** 创建时间 */
    private java.time.LocalDateTime createTime;
    /** 更新时间 */
    private java.time.LocalDateTime updateTime;

    public java.util.Date getCreateTimeAsDate() {
        return createTime == null ? null : java.sql.Timestamp.valueOf(createTime);
    }

    public java.util.Date getUpdateTimeAsDate() {
        return updateTime == null ? null : java.sql.Timestamp.valueOf(updateTime);
    }

    /**
     * 发布 API 到网关
     */
    public void publish() {
        if (this.endpoints == null || this.endpoints.isEmpty()) {
            throw new IllegalStateException("API 资产至少包含一个端点才能发布");
        }
        this.status = ApiStatusEnum.PUBLISHED;
        this.updateTime = java.time.LocalDateTime.now();
    }

    /**
     * 下架 API
     */
    public void offline() {
        if (ApiStatusEnum.OFFLINE.equals(this.status)) {
            throw new IllegalStateException("API 已处于下架状态");
        }
        this.status = ApiStatusEnum.OFFLINE;
        this.updateTime = java.time.LocalDateTime.now();
    }

    /**
     * 废弃 API
     */
    public void deprecate() {
        if (!ApiStatusEnum.PUBLISHED.equals(this.status)) {
            throw new IllegalStateException("仅已发布的 API 可以标记为废弃");
        }
        this.status = ApiStatusEnum.DEPRECATED;
        this.updateTime = java.time.LocalDateTime.now();
    }
}
