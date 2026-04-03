package com.api.expose.system.controller.admin.dept;

import com.api.expose.framework.common.enums.CommonStatusEnum;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.util.object.BeanUtils;
import com.api.expose.framework.datapermission.core.annotation.DataPermission;
import com.api.expose.framework.excel.core.util.ExcelUtils;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.api.expose.system.dal.dataobject.dept.DeptDO;
import com.api.expose.system.dal.dataobject.user.AdminUserDO;
import com.api.expose.system.service.dept.DeptService;
import com.api.expose.system.service.user.AdminUserService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.api.expose.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 部门")
@RestController
@RequestMapping("/system/dept")
@Validated
public class DeptController {

    @Resource
    private DeptService deptService;

    @Resource
    private AdminUserService adminUserService;

    @PostMapping("create")
    @Operation(summary = "创建部门")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public CommonResult<Long> createDept(@Valid @RequestBody DeptSaveReqVO createReqVO) {
        Long deptId = deptService.createDept(createReqVO);
        return success(deptId);
    }

    @PutMapping("update")
    @Operation(summary = "更新部门")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public CommonResult<Boolean> updateDept(@Valid @RequestBody DeptSaveReqVO updateReqVO) {
        deptService.updateDept(updateReqVO);
        return success(true);
    }

    @DeleteMapping("delete")
    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDept(@RequestParam("id") Long id) {
        deptService.deleteDept(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取部门列表")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    @DataPermission(enable = false)
    public CommonResult<List<DeptRespVO>> getDeptList(DeptListReqVO reqVO) {
        List<DeptDO> list =  deptService.getDeptList(reqVO);
        // 1. 批量收集所有负责人ID
        Set<Long> allUserIds = list.stream()
                .filter(dept -> dept.getLeaderUserIds() != null)
                .flatMap(dept -> dept.getLeaderUserIds().stream())
                .collect(Collectors.toSet());

        // 2. 批量查用户
        Map<Long, String> userIdNameMap = new HashMap<>();
        if (!allUserIds.isEmpty()) {
            List<AdminUserDO> users = adminUserService.getUserList(allUserIds);
            userIdNameMap = users.stream().collect(
                    Collectors.toMap(AdminUserDO::getId, AdminUserDO::getNickname)
            );
        }
        return success(deptService.convertLeaderName(list, userIdNameMap));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取部门精简信息列表", description = "只包含被开启的部门，主要用于前端的下拉选项")
    public CommonResult<List<DeptSimpleRespVO>> getSimpleDeptList() {
        List<DeptDO> list = deptService.getDeptList(
                new DeptListReqVO().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(BeanUtils.toBean(list, DeptSimpleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "部门管理 - 导出")
    @PreAuthorize("@ss.hasPermission('system:dept:export')")
    public void exportExcel(HttpServletResponse response, @Validated DeptListReqVO reqVO) throws IOException {
        // 查询全部部门（可根据 reqVO 条件筛选）
        List<DeptDO> list = deptService.getDeptList(reqVO);
        // 导出
        // 1. 批量收集所有负责人ID
        Set<Long> allUserIds = list.stream()
                .filter(dept -> dept.getLeaderUserIds() != null)
                .flatMap(dept -> dept.getLeaderUserIds().stream())
                .collect(Collectors.toSet());

        // 2. 批量查用户
        Map<Long, String> userIdNameMap = new HashMap<>();
        if (!allUserIds.isEmpty()) {
            List<AdminUserDO> users = adminUserService.getUserList(allUserIds);
            userIdNameMap = users.stream().collect(
                    Collectors.toMap(AdminUserDO::getId, AdminUserDO::getNickname)
            );
        }
        List<DeptRespVO> voList = deptService.convertLeaderName(list, userIdNameMap);
        ExcelUtils.write(response, "部门数据.xls", "部门列表", DeptRespVO.class, voList);
    }

    @GetMapping("/get")
    @Operation(summary = "获得部门信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<DeptRespVO> getDept(@RequestParam("id") Long id) {
        DeptDO dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DeptRespVO.class));
    }

    @GetMapping("/getChildDept")
    @Operation(summary = "获得下级信息")
    @Parameter(name = "pid", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<List<DeptRespVO>> getChildDept(@RequestParam("pid") Long pid) {
       List<DeptDO> list = deptService.getChildDept(pid);
       return success(BeanUtils.toBean(list, DeptRespVO.class));
    }

    @GetMapping("/list-by-business-module")
    @Operation(summary = "根据业务模块获取部门列表", description = "获取业务表中实际使用的部门")
    @Parameter(name = "moduleCode", description = "业务模块编码", required = true, example = "RECRUIT_DEMAND")
    public CommonResult<List<DeptSimpleRespVO>> getDeptListByBusinessModule(@RequestParam("moduleCode") String moduleCode) {
        List<DeptDO> list = deptService.getDeptsByBusinessModule(moduleCode);
        return success(BeanUtils.toBean(list, DeptSimpleRespVO.class));
    }

    @PostMapping("/fix-dept-level-names")
    @Operation(summary = "修复部门层级全称", description = "为现有部门数据填充部门层级全称字段")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public CommonResult<Boolean> fixDeptLevelNames() {
        int updatedCount = deptService.fixDeptLevelNames();
        return success(true);
    }
}
