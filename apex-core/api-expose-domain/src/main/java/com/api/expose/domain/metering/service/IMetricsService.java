package com.api.expose.domain.metering.service;

import java.util.Map;

/**
 * 运营指标服务
 */
public interface IMetricsService {

    /**
     * 获取近 N 天流量趋势
     */
    Map<String, Long> getTrafficTrend(int days);

}
