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
 * API 版本持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_api_version")
public class ApiVersionPO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 所属资产ID */
    private Long assetId;
    /** 版本号 */
    private String version;
    /** 是否当前生效版本 (1-是, 0-否) */
    private Integer active;
    /** 发布日志 */
    private String releaseNote;
    /** 创建时间 */
    private Date createTime;
}
