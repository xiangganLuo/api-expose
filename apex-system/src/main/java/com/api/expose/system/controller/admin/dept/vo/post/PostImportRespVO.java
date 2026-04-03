package com.api.expose.system.controller.admin.dept.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 岗位导入 Response VO
 */
@Schema(description = "管理后台 - 岗位导入 Response VO")
@Data
@Builder
public class PostImportRespVO {

    @Schema(description = "创建成功的岗位数组")
    private List<String> createNames;

    @Schema(description = "更新成功的岗位数组")
    private List<String> updateNames;

    @Schema(description = "导入失败的岗位数组")
    private List<String> failureNames;

}