package com.api.expose.system.controller.admin.dept.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 部门列表 Request VO")
@Data
public class DeptListReqVO {

    @Schema(description = "部门名称，模糊匹配", example = "APEX")
    private String name;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

    @Schema(description = "部门类型，参见 system_dept_type 字典", example = "1")
    private String deptType;

}
