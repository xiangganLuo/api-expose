package com.api.expose.domain.metering.adapter.repository;

import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * API 调用记录仓储接口
 */
public interface IApiCallRecordRepository {

    /**
     * 保存调用记录
     */
    void saveRecord(ApiCallRecordEntity record);

    /**
     * 查询近期流量趋势 (最近 N 天)
     * @return key:日期, value:调用次数
     */
    Map<String, Long> queryTrafficTrend(int days);

    /**
     * 查询应用的聚合指标概览
     */
    Map<String, Object> queryAppMetricsOverview(Long appId, int days);

    /**
     * 查询应用的调用趋势
     */
    List<Map<String, Object>> queryAppMetricsTrend(Long appId, int days);

    /**
     * 查询资产的聚合指标概览
     */
    Map<String, Object> queryAssetMetricsOverview(Long apiAssetId, int days);

    /**
     * 查询资产的调用趋势
     */
    List<Map<String, Object>> queryAssetMetricsTrend(Long apiAssetId, int days);

    /**
     * 按时间段查询调用记录（用于计费）
     */
    List<ApiCallRecordEntity> queryRecordsByPeriod(Date startDate, Date endDate);

}
