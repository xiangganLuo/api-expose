package com.api.expose.system.controller.admin.user;

import cn.hutool.core.collection.CollUtil;
import com.api.expose.framework.apilog.core.annotation.ApiAccessLog;
import com.api.expose.framework.common.enums.CommonStatusEnum;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.pojo.PageParam;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.framework.common.util.object.BeanUtils;
import com.api.expose.framework.datapermission.core.annotation.DataPermission;
import com.api.expose.framework.excel.core.util.ExcelUtils;
import com.api.expose.system.controller.admin.user.vo.user.*;
import com.api.expose.system.convert.user.UserConvert;
import com.api.expose.system.convert.user.UserConverts;
import com.api.expose.system.dal.dataobject.dept.DeptDO;
import com.api.expose.system.dal.dataobject.user.AdminUserDO;
import com.api.expose.system.enums.common.SexEnum;
import com.api.expose.system.service.dept.DeptService;
import com.api.expose.system.service.permission.PermissionService;
import com.api.expose.system.service.user.AdminUserService;
import com.api.expose.system.util.PostNamesStrFieldProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static com.api.expose.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.api.expose.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 用户")
@RestController
@RequestMapping("/system/user")
@Validated
public class UserController {

    @Resource
    private AdminUserService userService;
    @Resource
    private DeptService deptService;
    @Resource
    private PostNamesStrFieldProcessor postNamesStrFieldProcessor;
    @Resource
    private PermissionService permissionService;


    @PostMapping("/create")
    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserSaveReqVO reqVO) {
        Long id = userService.createUser(reqVO);
        return success(id);
    }

    @PutMapping("update")
    @Operation(summary = "修改用户")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserSaveReqVO reqVO) {
        userService.updateUser(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @Operation(summary = "批量删除用户")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public CommonResult<Boolean> deleteUserList(@RequestParam("ids") List<Long> ids) {
        userService.deleteUserList(ids);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "重置用户密码")
    @PreAuthorize("@ss.hasPermission('system:user:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(reqVO.getId(), reqVO.getPassword());
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "修改用户状态")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
        userService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户分页列表")
    @DataPermission(enable = false)
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO pageReqVO) {
        // 获得用户分页列表
        PageResult<AdminUserDO> pageResult = userService.getUserPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        // 拼接数据
        LinkedHashSet<Long> deptIds = new LinkedHashSet<>();
        pageResult.getList().forEach(user -> {
            if (CollUtil.isNotEmpty(user.getDeptIds())) {
                deptIds.addAll(user.getDeptIds());
            }
        });
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(deptIds);
        // 1. 先转换为VO
        List<UserRespVO> voList = UserConverts.INSTANCE.convertList(pageResult.getList(), deptMap);
        // 2. 用工具类处理注解字段
        voList.forEach(postNamesStrFieldProcessor::process);
        // 3. 返回
        return success(new PageResult<>(voList, pageResult.getTotal()));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取用户精简信息列表", description = "只包含被开启的用户，主要用于前端的下拉选项")
    @DataPermission(enable = false)
    public CommonResult<List<UserSimpleRespVO>> getSimpleUserList() {
        List<AdminUserDO> list = userService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus());
        // 拼接数据
        LinkedHashSet<Long> deptIds = new LinkedHashSet<>();
        list.forEach(user -> {
            if (CollUtil.isNotEmpty(user.getDeptIds())) {
                deptIds.addAll(user.getDeptIds());
            }
        });
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(deptIds);
        return success(UserConverts.INSTANCE.convertSimpleList(list, deptMap));
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<UserRespVO> getUser(@RequestParam("id") Long id) {
        AdminUserDO user = userService.getUser(id);
        if (user == null) {
            return success(null);
        }
        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(user.getDeptIds());
        return success(UserConverts.INSTANCE.convert(user, deptMap));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserList(@Validated UserPageReqVO exportReqVO,
                               HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AdminUserDO> list = userService.getUserPage(exportReqVO).getList();
        // 输出 Excel
        LinkedHashSet<Long> deptIds = new LinkedHashSet<>();
        list.forEach(user -> {
            if (CollUtil.isNotEmpty(user.getDeptIds())) {
                deptIds.addAll(user.getDeptIds());
            }
        });
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(deptIds);
        ExcelUtils.write(response, "用户数据.xls", "数据", UserRespVO.class,
                UserConvert.INSTANCE.convertList(list, deptMap));
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入用户模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        // 手动创建导出 demo
        List<UserImportExcelVO> list = Arrays.asList(
                UserImportExcelVO.builder().username("yunai").deptId(1L).deptIds("1,2").postCodes("tmldr,gmgr").email("yunai1@iocoder.cn").mobile("15601691310")
                        .nickname("APEX").status(CommonStatusEnum.ENABLE.getStatus()).sex(SexEnum.MALE.getSex()).build());
        // 输出
        ExcelUtils.write(response, "用户导入模板.xls", "用户列表", UserImportExcelVO.class, list);
    }

    @PostMapping("/import")
    @Operation(summary = "导入用户")
    @Parameters({
            @Parameter(name = "file", description = "Excel 文件", required = true),
            @Parameter(name = "updateSupport", description = "是否支持更新，默认为 false", example = "true")
    })
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public CommonResult<UserImportRespVO> importExcel(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws Exception {
        List<UserImportExcelVO> list = ExcelUtils.read(file, UserImportExcelVO.class);
        return success(userService.importUserList(list, updateSupport));
    }

    @GetMapping("/get-import-user-role-template")
    @Operation(summary = "获得用户角色绑定关系导入模板")
    public void importUserRoleTemplate(HttpServletResponse response) throws IOException {
        List<UserRoleImportExcelVO> list = new ArrayList<>();
        // 示例数据
        UserRoleImportExcelVO demo = new UserRoleImportExcelVO();
        demo.setUserId(1001L);
        demo.setRoleIds("1,2,3");
        list.add(demo);
        ExcelUtils.write(response, "用户角色绑定导入模板.xls", "用户角色绑定", UserRoleImportExcelVO.class, list);
    }

    @PostMapping("/import-user-role")
    @Operation(summary = "批量导入用户角色绑定关系")
    @Parameters({
            @Parameter(name = "file", description = "Excel 文件", required = true)
    })
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public CommonResult<UserRoleImportRespVO> importUserRoleExcel(@RequestParam("file") MultipartFile file) throws Exception {
        List<UserRoleImportExcelVO> list = ExcelUtils.read(file, UserRoleImportExcelVO.class);
        return success(permissionService.importUserRoleList(list));
    }

    @GetMapping("/list-by-business-module")
    @Operation(summary = "根据业务模块获取用户列表", description = "获取业务表中实际使用的用户")
    @Parameter(name = "moduleCode", description = "业务模块编码", required = true, example = "RECRUIT_DEMAND")
    public CommonResult<List<UserSimpleRespVO>> getUserListByBusinessModule(@RequestParam("moduleCode") String moduleCode) {
        List<AdminUserDO> list = userService.getUsersByBusinessModule(moduleCode);
        return success(BeanUtils.toBean(list, UserSimpleRespVO.class));
    }

}
