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
 * 开发者应用持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_developer_app")
public class DeveloperAppPO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 租户ID */
    private String tenantId;
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
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
