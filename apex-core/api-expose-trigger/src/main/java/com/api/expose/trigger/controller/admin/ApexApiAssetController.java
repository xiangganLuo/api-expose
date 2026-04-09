package com.api.expose.trigger.controller.admin;

import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.valobj.ApiStatusEnum;
import com.api.expose.domain.api.model.valobj.ProtocolTypeEnum;
import com.api.expose.domain.api.service.IApiAssetService;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiAssetImportReqVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiAssetPageReqVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiAssetRespVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiAssetUpdateReqVO;
import com.google.common.collect.Lists;
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

/*
 * @author xiangganluo
 */

import static com.api.expose.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - APEx - API 资产")
@RestController
@RequestMapping("/apex/assets")
@Validated
public class ApexApiAssetController {

    @Resource
    private IApiAssetService apiAssetService;

    @PostMapping("/import")
    @Operation(summary = "资产导入")
    @PreAuthorize("@ss.hasPermission('apex:asset:import')")
    public CommonResult<Long> importAsset(@Valid @RequestBody ApexApiAssetImportReqVO reqVO) {
        ApiAssetAggregate aggregate = ApiAssetAggregate.builder()
                .name(reqVO.getGroupName())
                .groupName(reqVO.getGroupName())
                .protocolType(ProtocolTypeEnum.HTTP)
                .status(ApiStatusEnum.DRAFT)
                .build();
        Long id = apiAssetService.importApiAndReturnId(aggregate, reqVO.getFileContent());
        return success(id);
    }

    @GetMapping("/page")
    @Operation(summary = "资产分页")
    @PreAuthorize("@ss.hasPermission('apex:asset:query')")
    public CommonResult<PageResult<ApexApiAssetRespVO>> getAssetPage(@Valid ApexApiAssetPageReqVO pageReqVO) {
        PageResult<ApiAssetAggregate> pageResult = apiAssetService.pageAssets(
                pageReqVO.getKeywords(), pageReqVO.getGroupName(), pageReqVO.getStatus(), pageReqVO);
        List<ApexApiAssetRespVO> list = pageResult.getList().stream().map(this::convert).collect(Collectors.toList());
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "资产详情")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:query')")
    public CommonResult<ApexApiAssetRespVO> getAsset(@RequestParam("id") Long id) {
        ApiAssetAggregate agg = apiAssetService.queryApiAssetDetail(id);
        return success(agg == null ? null : convert(agg));
    }

    @PutMapping("/update")
    @Operation(summary = "资产更新")
    @PreAuthorize("@ss.hasPermission('apex:asset:update')")
    public CommonResult<Boolean> updateAsset(@Valid @RequestBody ApexApiAssetUpdateReqVO reqVO) {
        ApiAssetAggregate aggregate = ApiAssetAggregate.builder()
                .assetId(reqVO.getId())
                .name(reqVO.getName())
                .description(reqVO.getDescription())
                .groupName(reqVO.getGroupName())
                .basePath(reqVO.getBasePath())
                .status(reqVO.getStatus() != null ? ApiStatusEnum.getEnumByCode(reqVO.getStatus()) : null)
                .build();
        apiAssetService.updateApiAsset(aggregate);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "资产删除")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:delete')")
    public CommonResult<Boolean> deleteAsset(@RequestParam("id") Long id) {
        apiAssetService.deleteApiAsset(id);
        return success(true);
    }

    @PostMapping("/publish")
    @Operation(summary = "资产发布")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:publish')")
    public CommonResult<Boolean> publish(@RequestParam("id") Long id) {
        apiAssetService.publishApi(id);
        return success(true);
    }

    @PostMapping("/offline")
    @Operation(summary = "资产下架")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:offline')")
    public CommonResult<Boolean> offline(@RequestParam("id") Long id) {
        apiAssetService.offlineApi(id);
        return success(true);
    }

    @PostMapping("/deprecate")
    @Operation(summary = "资产废弃")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:deprecate')")
    public CommonResult<Boolean> deprecate(@RequestParam("id") Long id) {
        apiAssetService.deprecateApi(id);
        return success(true);
    }

    private ApexApiAssetRespVO convert(ApiAssetAggregate agg) {
        ApexApiAssetRespVO vo = new ApexApiAssetRespVO();
        vo.setId(agg.getAssetId());
        vo.setName(agg.getName());
        vo.setDescription(agg.getDescription());
        vo.setGroupName(agg.getGroupName());
        vo.setProtocolType(agg.getProtocolType() != null ? agg.getProtocolType().name() : null);
        vo.setStatus(agg.getStatus() != null ? agg.getStatus().getCode() : null);
        vo.setBasePath(agg.getBasePath());
        vo.setCreateTime(agg.getCreateTime());
        vo.setUpdateTime(agg.getUpdateTime());
        return vo;
    }
}
