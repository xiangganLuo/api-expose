package com.api.expose.trigger.controller.admin;

import com.api.expose.domain.metering.model.valobj.MetricAggregationVO;
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
        MetricAggregationVO vo = metricsService.getAppMetrics(reqVO.getAppId(), reqVO.getDays());
        ApexMetricRespVO resp = ApexMetricRespVO.builder()
                .totalCalls(vo.getTotalCalls())
                .successCalls(vo.getSuccessCalls())
                .failCalls(vo.getFailCalls())
                .avgLatencyMs(vo.getAvgLatencyMs())
                .series(vo.getSeries().stream().map(p -> ApexMetricRespVO.MetricPoint.builder()
                        .time(p.getTime())
                        .calls(p.getCalls())
                        .successCalls(p.getSuccessCalls())
                        .failCalls(p.getFailCalls())
                        .avgLatencyMs(p.getAvgLatencyMs())
                        .build()).collect(java.util.stream.Collectors.toList()))
                .build();
        return success(resp);
    }

    @GetMapping("/asset")
    @Operation(summary = "按资产查询指标")
    @PreAuthorize("@ss.hasPermission('apex:metrics:query')")
    public CommonResult<ApexMetricRespVO> metricsByAsset(@Valid ApexMetricAssetReqVO reqVO) {
        MetricAggregationVO vo = metricsService.getAssetMetrics(reqVO.getApiAssetId(), reqVO.getDays());
        ApexMetricRespVO resp = ApexMetricRespVO.builder()
                .totalCalls(vo.getTotalCalls())
                .successCalls(vo.getSuccessCalls())
                .failCalls(vo.getFailCalls())
                .avgLatencyMs(vo.getAvgLatencyMs())
                .series(vo.getSeries().stream().map(p -> ApexMetricRespVO.MetricPoint.builder()
                        .time(p.getTime())
                        .calls(p.getCalls())
                        .successCalls(p.getSuccessCalls())
                        .failCalls(p.getFailCalls())
                        .avgLatencyMs(p.getAvgLatencyMs())
                        .build()).collect(java.util.stream.Collectors.toList()))
                .build();
        return success(resp);
    }
}
