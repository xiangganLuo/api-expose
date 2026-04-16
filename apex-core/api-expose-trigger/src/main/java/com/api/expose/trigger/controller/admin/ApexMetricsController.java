package com.api.expose.trigger.controller.admin;

import com.api.expose.domain.metering.service.IMetricsService;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.trigger.controller.admin.vo.metrics.ApexMetricAppReqVO;
import com.api.expose.trigger.controller.admin.vo.metrics.ApexMetricAssetReqVO;
import com.api.expose.trigger.controller.admin.vo.metrics.ApexMetricRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.Map;

import static com.api.expose.framework.common.pojo.CommonResult.success;

/*
 * @author xiangganluo
 */

@Tag(name = "管理后台 - APEx - 监控指标")
@RestController
@RequestMapping("/apex/metrics")
@Validated
public class ApexMetricsController {

    @Resource
    private IMetricsService metricsService;

    @GetMapping("/trend")
    @Operation(summary = "近 N 天调用趋势")
    @PreAuthorize("@ss.hasPermission('apex:metrics:query')")
    public CommonResult<Map<String, Long>> trend(@RequestParam("days") @Min(1) @Max(30) Integer days) {
        return success(metricsService.getTrafficTrend(days));
    }

    @GetMapping("/app")
    @Operation(summary = "按应用查询指标")
    @PreAuthorize("@ss.hasPermission('apex:metrics:query')")
    public CommonResult<ApexMetricRespVO> metricsByApp(@Valid ApexMetricAppReqVO reqVO) {
        // TODO 对接 IMetricsService 实现按 appId 维度的指标聚合
        ApexMetricRespVO resp = ApexMetricRespVO.builder()
                .totalCalls(0L).successCalls(0L).failCalls(0L)
                .avgLatencyMs(0.0).series(Collections.emptyList())
                .build();
        return success(resp);
    }

    @GetMapping("/asset")
    @Operation(summary = "按资产查询指标")
    @PreAuthorize("@ss.hasPermission('apex:metrics:query')")
    public CommonResult<ApexMetricRespVO> metricsByAsset(@Valid ApexMetricAssetReqVO reqVO) {
        // TODO 对接 IMetricsService 实现按 apiAssetId 维度的指标聚合
        ApexMetricRespVO resp = ApexMetricRespVO.builder()
                .totalCalls(0L).successCalls(0L).failCalls(0L)
                .avgLatencyMs(0.0).series(Collections.emptyList())
                .build();
        return success(resp);
    }
}
