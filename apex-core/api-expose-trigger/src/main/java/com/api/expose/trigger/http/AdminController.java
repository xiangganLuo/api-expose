package com.api.expose.trigger.http;

import com.api.expose.domain.metering.service.IMetricsService;
import com.api.expose.types.common.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 管理运营控制层
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Resource
    private IMetricsService metricsService;

    /**
     * 近期流量趋势图数据
     */
    @GetMapping("/dashboard/traffic-trend")
    public Response<Map<String, Long>> getTrafficTrend(@RequestParam(defaultValue = "7") int days) {
        return Response.success(metricsService.getTrafficTrend(days));
    }
}
