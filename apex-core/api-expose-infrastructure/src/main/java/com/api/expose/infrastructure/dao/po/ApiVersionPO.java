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
 * API 版本持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_api_version")
@EqualsAndHashCode(callSuper = true)
public class ApiVersionPO extends TenantBaseDO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;
    /** 所属资产ID */
    private Long assetId;
    /** 版本号 */
    private String version;
    /** 是否当前生效版本 (1-是, 0-否) */
    private Integer active;
    /** 发布日志 */
    private String releaseNote;
}
