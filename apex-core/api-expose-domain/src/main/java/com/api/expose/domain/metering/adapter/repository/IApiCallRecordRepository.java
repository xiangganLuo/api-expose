package com.api.expose.domain.metering.adapter.repository;

import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;

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

}
