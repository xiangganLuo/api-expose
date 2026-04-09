package com.api.expose.trigger.controller.admin;

import com.api.expose.domain.metering.service.IMetricsService;
import com.api.expose.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
}
