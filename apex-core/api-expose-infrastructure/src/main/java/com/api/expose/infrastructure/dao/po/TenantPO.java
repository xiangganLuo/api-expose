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
 * 租户持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_tenant")
public class TenantPO {

    /** 租户ID */
    @TableId(type = IdType.INPUT)
    private String tenantId;
    /** 租户名称 */
    private String tenantName;
    /** 自定义域名 */
    private String customDomain;
    /** Logo URL */
    private String logoUrl;
    /** 状态 (ACTIVE/SUSPENDED) */
    private String status;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
