package com.api.expose.trigger.controller.admin;

import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.valobj.AppStatusEnum;
import com.api.expose.domain.app.service.IAppService;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.trigger.controller.admin.vo.app.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.api.expose.framework.common.pojo.CommonResult.success;

/*
 * @author xiangganluo
 */

@Tag(name = "管理后台 - APEx - 应用")
@RestController
@RequestMapping("/apex/apps")
@Validated
public class ApexAppController {

    @Resource
    private IAppService appService;

    @PostMapping("/create")
    @Operation(summary = "应用创建")
    @PreAuthorize("@ss.hasPermission('apex:app:create')")
    public CommonResult<ApexAppCreateRespVO> create(@Valid @RequestBody ApexAppCreateReqVO reqVO) {
        DeveloperAppEntity entity = DeveloperAppEntity.builder()
                .appName(reqVO.getName())
                .description(reqVO.getDescription())
                .callbackUrl(reqVO.getCallbackUrl())
                .status(reqVO.getStatus() != null ? AppStatusEnum.getEnumByCode(reqVO.getStatus()) : null)
                .build();

        DeveloperAppEntity created = appService.createApp(entity);
        ApexAppCreateRespVO respVO = new ApexAppCreateRespVO();
        respVO.setAppId(created.getAppId());
        respVO.setApiKey(created.getApiKey());
        respVO.setApiSecret(created.getApiSecret());
        return success(respVO);
    }

    @PutMapping("/update")
    @Operation(summary = "应用更新")
    @PreAuthorize("@ss.hasPermission('apex:app:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody ApexAppUpdateReqVO reqVO) {
        DeveloperAppEntity entity = DeveloperAppEntity.builder()
                .appId(reqVO.getId())
                .appName(reqVO.getName())
                .description(reqVO.getDescription())
                .callbackUrl(reqVO.getCallbackUrl())
                .status(reqVO.getStatus() != null ? AppStatusEnum.getEnumByCode(reqVO.getStatus()) : null)
                .build();
        appService.updateApp(entity);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "应用删除")
    @Parameter(name = "id", description = "应用编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:app:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        appService.deleteApp(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "应用详情")
    @Parameter(name = "id", description = "应用编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:app:query')")
    public CommonResult<ApexAppRespVO> get(@RequestParam("id") Long id) {
        com.api.expose.domain.app.model.aggregate.DeveloperAppAggregate agg = appService.queryAppDetail(id);
        if (agg == null || agg.getApp() == null) {
            return success(null);
        }
        return success(convert(agg.getApp()));
    }

    @GetMapping("/page")
    @Operation(summary = "应用分页")
    @PreAuthorize("@ss.hasPermission('apex:app:query')")
    public CommonResult<PageResult<ApexAppRespVO>> page(@Valid ApexAppPageReqVO pageReqVO) {
        PageResult<DeveloperAppEntity> pageResult = appService.pageApps(pageReqVO.getKeywords(), pageReqVO.getStatus(), pageReqVO);
        List<ApexAppRespVO> list = pageResult.getList().stream().map(this::convert).collect(Collectors.toList());
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    private ApexAppRespVO convert(DeveloperAppEntity entity) {
        ApexAppRespVO vo = new ApexAppRespVO();
        vo.setId(entity.getAppId());
        vo.setName(entity.getAppName());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
        vo.setApiKey(entity.getApiKey());
        vo.setCallbackUrl(entity.getCallbackUrl());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
