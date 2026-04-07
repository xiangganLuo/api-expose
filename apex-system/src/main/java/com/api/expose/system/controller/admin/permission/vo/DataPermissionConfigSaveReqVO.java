package com.api.expose.system.controller.admin.permission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 数据权限配置新增/修改 Request VO")
@Data
public class DataPermissionConfigSaveReqVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "配置键(唯一标识)", requiredMode = Schema.RequiredMode.REQUIRED, example = "candidate_dept_filter")
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "候选人部门权限过滤")
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "candidate")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @Schema(description = "受限制的角色ID数组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "受限制的角色不能为空")
    private List<Long> roleIds;

    @Schema(description = "过滤字段名", requiredMode = Schema.RequiredMode.REQUIRED, example = "dept_id")
    @NotBlank(message = "过滤字段名不能为空")
    private String filterColumn;

    @Schema(description = "过滤表名(跨表时使用)", example = "system_user")
    private String filterTable;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "限制普通用户只能查看本部门候选人")
    private String remark;

}
