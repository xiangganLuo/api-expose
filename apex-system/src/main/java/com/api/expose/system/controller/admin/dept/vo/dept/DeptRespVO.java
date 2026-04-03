package com.api.expose.system.controller.admin.dept.vo.dept;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.api.expose.framework.excel.core.annotations.DictFormat;
import com.api.expose.framework.excel.core.convert.DictConvert;
import com.api.expose.system.enums.DictTypeConstants;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "管理后台 - 部门信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DeptRespVO {

    @ExcelProperty("部门编号")
    @Schema(description = "部门编号", example = "1024")
    private Long id;

    @ExcelProperty("部门名称")
    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "APEX")
    private String name;

    @ExcelProperty("父部门ID")
    @Schema(description = "父部门 ID", example = "1024")
    private Long parentId;

    @ExcelProperty("父部门名称")
    @Schema(description = "父部门名称", example = "1024")
    private String parentName;

    @ExcelProperty("显示顺序")
    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer sort;

    // 注意：这个字段不加 @ExcelProperty，EasyExcel 不会导出它，避免 Set<Long> 报错
    @Schema(description = "负责人的用户编号", example = "2048")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<Long> leaderUserIds;

    @ExcelProperty("负责人ID")
    private String leaderUserIdsStr;

    @ExcelProperty("负责人名称")
    @Schema(description = "负责人的用户名称", example = "2048")
    private String leaderUserNames;

    @ExcelProperty("联系电话")
    @Schema(description = "联系电话", example = "15601691000")
    private String phone;

    @ExcelProperty("邮箱")
    @Schema(description = "邮箱", example = "apex@iocoder.cn")
    private String email;

    @ExcelProperty("部门层级全称")
    @Schema(description = "部门层级全称", example = "研发部-前端组")
    private String deptLevelName;

    @ExcelProperty(value = "部门类型", converter = DictConvert.class)
    @Schema(description = "部门类型", example = "1")
    @DictFormat("system_dept_type")
    private String deptType;

    @ExcelProperty(value = "岗位状态", converter = DictConvert.class)
    @Schema(description = "状态,见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @ExcelProperty("创建时间")
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private LocalDateTime createTime;

}
