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
 * 开发者应用持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_developer_app")
@EqualsAndHashCode(callSuper = true)
public class DeveloperAppPO extends TenantBaseDO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;
    /** 应用名称 */
    private String appName;
    /** 应用描述 */
    private String description;
    /** 状态 (ACTIVE/INACTIVE) */
    private String status;
    /** API Key */
    private String apiKey;
    /** API Secret */
    private String apiSecret;
    /** 回调地址 */
    private String callbackUrl;
}
