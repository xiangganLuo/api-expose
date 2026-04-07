package com.api.expose.domain.api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * API 版本实体 - 支持多版本共存与平滑升级
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiVersionEntity {

    /** 版本ID */
    private Long versionId;
    /** 版本号 (如 v1, v2) */
    private String version;
    /** 是否为当前活跃版本 */
    private boolean active;
    /** 创建时间 */
    private Date createTime;
    /** 版本说明 */
    private String releaseNote;
}
