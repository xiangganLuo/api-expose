package com.api.expose.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
public class RouteRulePO {

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
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
