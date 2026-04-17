package com.api.expose.domain.metering.service;

import com.api.expose.domain.metering.model.valobj.MetricAggregationVO;

import java.util.Map;

/**
 * 运营指标服务
 */
public interface IMetricsService {

    /**
     * 获取近 N 天流量趋势 (全量)
     */
    Map<String, Long> getTrafficTrend(int days);

    /**
     * 获取应用级请求聚合指标
     */
    MetricAggregationVO getAppMetrics(Long appId, int days);

    /**
     * 获取资产级请求聚合指标
     */
    MetricAggregationVO getAssetMetrics(Long apiAssetId, int days);

}
