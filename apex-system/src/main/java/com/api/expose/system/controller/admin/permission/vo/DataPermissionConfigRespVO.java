package com.api.expose.system.controller.admin.permission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 数据权限配置 Response VO")
@Data
public class DataPermissionConfigRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "配置键(唯一标识)", requiredMode = Schema.RequiredMode.REQUIRED, example = "candidate_dept_filter")
    private String configKey;

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "候选人部门权限过滤")
    private String configName;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "candidate")
    private String businessType;

    @Schema(description = "受限制的角色ID数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;

    @Schema(description = "过滤字段名", requiredMode = Schema.RequiredMode.REQUIRED, example = "dept_id")
    private String filterColumn;

    @Schema(description = "过滤表名(跨表时使用)", example = "system_user")
    private String filterTable;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "备注", example = "限制普通用户只能查看本部门候选人")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "创建者", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creator;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    @Schema(description = "更新者", requiredMode = Schema.RequiredMode.REQUIRED)
    private String updater;

}




