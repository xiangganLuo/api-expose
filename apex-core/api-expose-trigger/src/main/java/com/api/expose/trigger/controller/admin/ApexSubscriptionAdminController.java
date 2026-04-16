package com.api.expose.trigger.controller.admin;

import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import com.api.expose.domain.app.service.IAppService;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.trigger.controller.admin.vo.subscription.ApexSubscriptionCreateReqVO;
import com.api.expose.trigger.controller.admin.vo.subscription.ApexSubscriptionPageReqVO;
import com.api.expose.trigger.controller.admin.vo.subscription.ApexSubscriptionRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.stream.Collectors;

import static com.api.expose.framework.common.pojo.CommonResult.success;
import static com.api.expose.framework.tenant.core.context.TenantContextHolder.getTenantId;

/*
 * @author xiangganluo
 */

@Tag(name = "管理后台 - APEx - 订阅")
@RestController
@RequestMapping("/apex/subscriptions")
@Validated
public class ApexSubscriptionAdminController {

    @Resource
    private IAppService appService;

    @PostMapping("/create")
    @Operation(summary = "新增订阅(分配权限)")
    @PreAuthorize("@ss.hasPermission('apex:subscription:manage')")
    public CommonResult<Long> create(@Valid @RequestBody ApexSubscriptionCreateReqVO reqVO) {
        String status = reqVO.getStatus() != null ? reqVO.getStatus() : SubscriptionStatusEnum.PENDING.getCode();
        SubscriptionEntity entity = SubscriptionEntity.builder()
                .tenantId(getTenantId())
                .appId(reqVO.getAppId())
                .apiAssetId(reqVO.getApiAssetId())
                .status(SubscriptionStatusEnum.getEnumByCode(status))
                .remark(reqVO.getRemark())
                .build();
        Long id = appService.applySubscription(entity);
        if (SubscriptionStatusEnum.APPROVED.getCode().equalsIgnoreCase(status)) {
            appService.auditSubscription(id, SubscriptionStatusEnum.APPROVED, reqVO.getRemark());
        }
        return success(id);
    }

    @PutMapping("/approve")
    @Operation(summary = "订阅通过")
    @Parameter(name = "id", description = "订阅编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:subscription:manage')")
    public CommonResult<Boolean> approve(@RequestParam("id") Long id) {
        appService.auditSubscription(id, SubscriptionStatusEnum.APPROVED, null);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "订阅驳回")
    @Parameter(name = "id", description = "订阅编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:subscription:manage')")
    public CommonResult<Boolean> reject(@RequestParam("id") Long id,
                                        @RequestParam(value = "remark", required = false) String remark) {
        appService.auditSubscription(id, SubscriptionStatusEnum.REJECTED, remark);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "订阅分页")
    @PreAuthorize("@ss.hasPermission('apex:subscription:query')")
    public CommonResult<PageResult<ApexSubscriptionRespVO>> page(@Valid ApexSubscriptionPageReqVO pageReqVO) {
        PageResult<SubscriptionEntity> pageResult = appService.getSubscriptionPage(
                pageReqVO.getAppId(), pageReqVO.getApiAssetId(), pageReqVO.getStatus(), pageReqVO);
        return success(new PageResult<>(pageResult.getList().stream().map(this::convert).collect(Collectors.toList()), pageResult.getTotal()));
    }

    private ApexSubscriptionRespVO convert(SubscriptionEntity entity) {
        ApexSubscriptionRespVO vo = new ApexSubscriptionRespVO();
        vo.setId(entity.getId());
        vo.setAppId(entity.getAppId());
        vo.setApiAssetId(entity.getApiAssetId());
        vo.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
