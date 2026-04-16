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

/**
 * API 资产环境配置持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_api_asset_env")
@EqualsAndHashCode(callSuper = true)
public class ApiAssetEnvPO extends TenantBaseDO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;
    /** 资产ID */
    private Long assetId;
    /** 环境标识 (如: test, prod) */
    private String envCode;
    /** 环境名称 (如: 测试环境) */
    private String envName;
    /** 后端基础路径 (如: http://api.test.com) */
    private String baseUrl;
    /** 状态 (0-正常, 1-关闭) */
    private Integer status;
}
