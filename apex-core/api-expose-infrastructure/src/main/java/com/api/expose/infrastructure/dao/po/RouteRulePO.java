package com.api.expose.infrastructure.dao.po;

import com.api.expose.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * @author xiangganluo
 */

/**
 * 网关路由规则持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_route_rule")
@EqualsAndHashCode(callSuper = true)
public class RouteRulePO extends TenantBaseDO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** API资产ID */
    private Long apiAssetId;
    /** 端点ID */
    private Long endpointId;
    /** 请求方法 */
    private String httpMethod;
    /** 网关路径 */
    private String gatewayPath;
    /** 上游路径 */
    private String upstreamPath;
    /** 上游真实地址 */
    private String upstreamUrl;
    /** 状态 (ACTIVE/INACTIVE) */
    private String status;
}
