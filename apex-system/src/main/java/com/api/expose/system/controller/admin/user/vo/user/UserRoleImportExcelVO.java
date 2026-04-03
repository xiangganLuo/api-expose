package com.api.expose.system.controller.admin.user.vo.user;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false) // 设置 chain = false，避免用户导入有问题
public class UserRoleImportExcelVO {

    @ExcelProperty("用户ID")
    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @ExcelProperty("角色ID列表")
    @Schema(description = "角色ID列表，多个用英文逗号分隔", example = "1,2,3")
    private String roleIds;
}
