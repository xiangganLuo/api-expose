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
 * API 资产持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_api_asset")
public class ApiAssetPO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 租户ID */
    private String tenantId;
    /** 资产名称 */
    private String name;
    /** 资产描述 */
    private String description;
    /** 分组名称 */
    private String groupName;
    /** 协议类型 (HTTP/HTTPS) */
    private String protocolType;
    /** 状态 (DRAFT/PUBLISHED/DEPRECATED/OFFLINE) */
    private String status;
    /** 基础路径 */
    private String basePath;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
