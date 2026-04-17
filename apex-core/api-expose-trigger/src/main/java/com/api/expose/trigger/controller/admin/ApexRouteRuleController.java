package com.api.expose.trigger.controller.admin;

import com.api.expose.domain.api.adapter.repository.IApiRouteRepository;
import com.api.expose.domain.api.model.valobj.RouteRuleVO;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.trigger.controller.admin.vo.route.ApexRouteRulePageReqVO;
import com.api.expose.trigger.controller.admin.vo.route.ApexRouteRuleRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.api.expose.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - APEx - 网关路由")
@RestController
@RequestMapping("/apex/routes")
@Validated
public class ApexRouteRuleController {

    @Resource
    private IApiRouteRepository apiRouteRepository;

    @GetMapping("/page")
    @Operation(summary = "路由规则分页")
    @PreAuthorize("@ss.hasPermission('apex:route:query')")
    public CommonResult<PageResult<ApexRouteRuleRespVO>> getRoutePage(@Valid ApexRouteRulePageReqVO pageReqVO) {
        PageResult<RouteRuleVO> pageResult = apiRouteRepository.pageRouteRules(pageReqVO.getKeywords(), pageReqVO);
        List<ApexRouteRuleRespVO> list = pageResult.getList().stream().map(vo -> {
            ApexRouteRuleRespVO respVO = new ApexRouteRuleRespVO();
            respVO.setId(vo.getId());
            respVO.setApiAssetId(vo.getApiAssetId());
            respVO.setEndpointId(vo.getEndpointId());
            respVO.setHttpMethod(vo.getHttpMethod());
            respVO.setGatewayPath(vo.getGatewayPath());
            respVO.setUpstreamPath(vo.getUpstreamPath());
            respVO.setUpstreamUrl(vo.getUpstreamUrl());
            respVO.setStatus(vo.getStatus());
            return respVO;
        }).collect(Collectors.toList());
        return success(new PageResult<>(list, pageResult.getTotal()));
    }
}
