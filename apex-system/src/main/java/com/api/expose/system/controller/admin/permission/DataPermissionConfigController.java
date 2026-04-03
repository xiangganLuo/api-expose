package com.api.expose.system.controller.admin.permission;

import com.api.expose.framework.apilog.core.annotation.ApiAccessLog;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.pojo.PageParam;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.framework.excel.core.util.ExcelUtils;
import com.api.expose.system.controller.admin.permission.vo.DataPermissionConfigPageReqVO;
import com.api.expose.system.controller.admin.permission.vo.DataPermissionConfigRespVO;
import com.api.expose.system.controller.admin.permission.vo.DataPermissionConfigSaveReqVO;
import com.api.expose.system.convert.permission.DataPermissionConfigConvert;
import com.api.expose.system.dal.dataobject.permission.DataPermissionConfigDO;
import com.api.expose.system.service.permission.DataPermissionConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.api.expose.framework.apilog.core.enums.OperateTypeEnum.*;
import static com.api.expose.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据权限配置")
@RestController
@RequestMapping("/system/data-permission-config")
@Validated
public class DataPermissionConfigController {

    @Resource
    private DataPermissionConfigService dataPermissionConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建数据权限配置")
    @PreAuthorize("@ss.hasPermission('system:data-permission-config:create')")
    @ApiAccessLog(operateType = CREATE)
    public CommonResult<Long> createDataPermissionConfig(@Valid @RequestBody DataPermissionConfigSaveReqVO createReqVO) {
        return success(dataPermissionConfigService.createDataPermissionConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新数据权限配置")
    @PreAuthorize("@ss.hasPermission('system:data-permission-config:update')")
    @ApiAccessLog(operateType = UPDATE)
    public CommonResult<Boolean> updateDataPermissionConfig(@Valid @RequestBody DataPermissionConfigSaveReqVO updateReqVO) {
        dataPermissionConfigService.updateDataPermissionConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据权限配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:data-permission-config:delete')")
    @ApiAccessLog(operateType = DELETE)
    public CommonResult<Boolean> deleteDataPermissionConfig(@RequestParam("id") Long id) {
        dataPermissionConfigService.deleteDataPermissionConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得数据权限配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:data-permission-config:query')")
    public CommonResult<DataPermissionConfigRespVO> getDataPermissionConfig(@RequestParam("id") Long id) {
        DataPermissionConfigDO config = dataPermissionConfigService.getDataPermissionConfig(id);
        return success(DataPermissionConfigConvert.INSTANCE.convert(config));
    }

    @GetMapping("/page")
    @Operation(summary = "获得数据权限配置分页")
    @PreAuthorize("@ss.hasPermission('system:data-permission-config:query')")
    public CommonResult<PageResult<DataPermissionConfigRespVO>> getDataPermissionConfigPage(@Valid DataPermissionConfigPageReqVO pageReqVO) {
        PageResult<DataPermissionConfigDO> pageResult = dataPermissionConfigService.getDataPermissionConfigPage(pageReqVO);
        return success(DataPermissionConfigConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获得数据权限配置列表")
    @PreAuthorize("@ss.hasPermission('system:data-permission-config:query')")
    public CommonResult<List<DataPermissionConfigRespVO>> getDataPermissionConfigList() {
        List<DataPermissionConfigDO> list = dataPermissionConfigService.getDataPermissionConfigList();
        return success(DataPermissionConfigConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出数据权限配置 Excel")
    @PreAuthorize("@ss.hasPermission('system:data-permission-config:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDataPermissionConfigExcel(DataPermissionConfigPageReqVO pageReqVO,
                                               HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DataPermissionConfigDO> list = dataPermissionConfigService.getDataPermissionConfigPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "数据权限配置.xls", "数据", DataPermissionConfigRespVO.class,
                DataPermissionConfigConvert.INSTANCE.convertList(list));
    }

}
