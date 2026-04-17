package com.api.expose.trigger.controller.admin;

import com.api.expose.domain.policy.adapter.repository.IPolicyRepository;
import com.api.expose.domain.policy.model.aggregate.GovernancePolicyAggregate;
import com.api.expose.domain.policy.model.valobj.PolicyScopeEnum;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.infrastructure.dao.IGovernancePolicyDao;
import com.api.expose.infrastructure.dao.po.GovernancePolicyPO;
import com.api.expose.trigger.controller.admin.vo.policy.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import static com.api.expose.framework.tenant.core.context.TenantContextHolder.getTenantId;

/*
 * @author xiangganluo
 */

@Tag(name = "管理后台 - APEx - 治理策略")
@RestController
@RequestMapping("/apex/policies")
@Validated
public class ApexPolicyController {

    @Resource
    private IPolicyRepository policyRepository;

    @Resource
    private IGovernancePolicyDao governancePolicyDao;

    @PostMapping("/create")
    @Operation(summary = "创建策略")
    @PreAuthorize("@ss.hasPermission('apex:policy:create')")
    public CommonResult<Long> create(@Valid @RequestBody ApexPolicyCreateReqVO reqVO) {
        GovernancePolicyAggregate aggregate = toAggregate(reqVO);
        aggregate.setTenantId(getTenantId());
        policyRepository.savePolicy(aggregate);
        return success(aggregate.getPolicyId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新策略")
    @PreAuthorize("@ss.hasPermission('apex:policy:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody ApexPolicyUpdateReqVO reqVO) {
        GovernancePolicyAggregate aggregate = toAggregate(reqVO);
        aggregate.setPolicyId(reqVO.getId());
        aggregate.setTenantId(getTenantId());
        policyRepository.updatePolicy(aggregate);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除策略")
    @Parameter(name = "id", description = "策略编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:policy:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        governancePolicyDao.deleteById(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "策略详情")
    @Parameter(name = "id", description = "策略编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:policy:query')")
    public CommonResult<ApexPolicyRespVO> get(@RequestParam("id") Long id) {
        GovernancePolicyPO po = governancePolicyDao.selectById(id);
        return success(po == null ? null : convert(po));
    }

    @GetMapping("/page")
    @Operation(summary = "策略分页")
    @PreAuthorize("@ss.hasPermission('apex:policy:query')")
    public CommonResult<PageResult<ApexPolicyRespVO>> page(@Valid ApexPolicyPageReqVO reqVO) {
        LambdaQueryWrapper<GovernancePolicyPO> wrapper = new LambdaQueryWrapper<>();
        if (reqVO.getScope() != null) {
            wrapper.eq(GovernancePolicyPO::getScope, reqVO.getScope());
        }
        if (reqVO.getApiAssetId() != null) {
            wrapper.eq(GovernancePolicyPO::getApiAssetId, reqVO.getApiAssetId());
        }
        if (reqVO.getAppId() != null) {
            wrapper.eq(GovernancePolicyPO::getAppId, reqVO.getAppId());
        }
        if (reqVO.getEnabled() != null) {
            wrapper.eq(GovernancePolicyPO::getEnabled, reqVO.getEnabled());
        }
        wrapper.orderByDesc(GovernancePolicyPO::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<GovernancePolicyPO> page =
                governancePolicyDao.selectPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(reqVO.getPageNo(), reqVO.getPageSize()), wrapper);
        List<ApexPolicyRespVO> list = page.getRecords().stream().map(this::convert).collect(Collectors.toList());
        return success(new PageResult<>(list, page.getTotal()));
    }

    @PutMapping("/update-status")
    @Operation(summary = "启停用")
    @PreAuthorize("@ss.hasPermission('apex:policy:update')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("enabled") Boolean enabled) {
        GovernancePolicyPO po = GovernancePolicyPO.builder().id(id).enabled(enabled).build();
        governancePolicyDao.updateById(po);
        return success(true);
    }

    private GovernancePolicyAggregate toAggregate(ApexPolicyCreateReqVO reqVO) {
        return GovernancePolicyAggregate.builder()
                .policyName(reqVO.getPolicyName())
                .scope(reqVO.getScope() != null ? PolicyScopeEnum.valueOf(reqVO.getScope()) : null)
                .apiAssetId(reqVO.getApiAssetId())
                .appId(reqVO.getAppId())
                .enabled(reqVO.getEnabled() != null ? reqVO.getEnabled() : Boolean.TRUE)
                .rateLimitJson(reqVO.getRateLimitJson())
                .circuitBreakerJson(reqVO.getCircuitBreakerJson())
                .accessControlJson(reqVO.getAccessControlJson())
                .build();
    }

    private ApexPolicyRespVO convert(GovernancePolicyPO po) {
        ApexPolicyRespVO vo = new ApexPolicyRespVO();
        vo.setId(po.getId());
        vo.setPolicyName(po.getPolicyName());
        vo.setScope(po.getScope());
        vo.setApiAssetId(po.getApiAssetId());
        vo.setAppId(po.getAppId());
        vo.setEnabled(po.getEnabled());
        vo.setRateLimitJson(po.getRateLimitJson());
        vo.setCircuitBreakerJson(po.getCircuitBreakerJson());
        vo.setAccessControlJson(po.getAccessControlJson());
        vo.setCreateTime(po.getCreateTime());
        vo.setUpdateTime(po.getUpdateTime());
        return vo;
    }
}
