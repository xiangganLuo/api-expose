package com.api.expose.domain.gateway.model.aggregate;

import com.api.expose.domain.gateway.model.valobj.PathMappingVO;
import com.api.expose.domain.gateway.model.valobj.RouteStatusEnum;
import com.api.expose.domain.gateway.model.valobj.UpstreamTargetVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 路由规则聚合根 - 网关运行时的核心路由配置
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteRuleAggregate {

    /** 路由ID */
    private Long routeId;
    /** 关联的 API 资产ID */
    private Long apiAssetId;
    /** 关联的端点ID */
    private Long endpointId;
    /** HTTP 方法 */
    private String httpMethod;
    /** 路径映射 */
    private PathMappingVO pathMapping;
    /** 上游目标 */
    private UpstreamTargetVO upstreamTarget;
    /** 路由状态 */
    private RouteStatusEnum status;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

    /**
     * 激活路由
     */
    public void activate() {
        this.status = RouteStatusEnum.ACTIVE;
        this.updateTime = new Date();
    }

    /**
     * 停用路由
     */
    public void deactivate() {
        this.status = RouteStatusEnum.INACTIVE;
        this.updateTime = new Date();
    }
}
