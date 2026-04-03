package com.api.expose.system.controller.admin.dept.vo.post;

import cn.idev.excel.annotation.ExcelProperty;
import com.api.expose.framework.excel.core.annotations.DictFormat;
import com.api.expose.framework.excel.core.convert.DictConvert;
import com.api.expose.system.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 岗位信息 Response VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false) // 设置 chain = false，避免用户导入有问题
public class PostImportExcelVO {

    @ExcelProperty("岗位序号")
    private Long id;

    @ExcelProperty("岗位编码")
    private String code;

    @ExcelProperty("岗位名称")
    private String name;

    @ExcelProperty("岗位排序")
    private Integer sort;

    @ExcelProperty(value = "岗位状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @ExcelProperty("备注")
    private String remark;

    private LocalDateTime createTime;

}
