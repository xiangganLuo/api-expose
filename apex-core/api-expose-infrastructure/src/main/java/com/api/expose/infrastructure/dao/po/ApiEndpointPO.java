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
 * API 端点持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_api_endpoint")
@EqualsAndHashCode(callSuper = true)
public class ApiEndpointPO extends TenantBaseDO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;
    /** 所属资产ID */
    private Long assetId;
    /** 端点路径 */
    private String path;
    /** HTTP方法 */
    private String httpMethod;
    /** 端点名称 */
    private String name;
    /** 精简描述 */
    private String summary;
    /** 请求结构(JSON) */
    private String requestSchema;
    /** 响应结构(JSON) */
    private String responseSchema;
    /** 上游后端地址 */
    private String upstreamUrl;
    /** 超时时间(ms) */
    private Integer timeoutMs;
}
