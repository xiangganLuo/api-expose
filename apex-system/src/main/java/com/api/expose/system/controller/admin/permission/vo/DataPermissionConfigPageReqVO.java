package com.api.expose.system.controller.admin.permission.vo;

import com.api.expose.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 数据权限配置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DataPermissionConfigPageReqVO extends PageParam {

    @Schema(description = "配置键", example = "candidate_dept_filter")
    private String configKey;

    @Schema(description = "配置名称", example = "候选人部门权限过滤")
    private String configName;

    @Schema(description = "业务类型", example = "candidate")
    private String businessType;

    @Schema(description = "状态", example = "1")
    private Integer status;

}



