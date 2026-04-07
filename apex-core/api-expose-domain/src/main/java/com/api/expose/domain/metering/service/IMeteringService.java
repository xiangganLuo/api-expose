package com.api.expose.domain.metering.service;

import com.api.expose.domain.metering.model.aggregate.MeteringStatAggregate;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;

import java.util.Date;

/**
 * 计量服务接口
 */
public interface IMeteringService {

    /**
     * 记录一次 API 调用
     */
    void recordApiCall(ApiCallRecordEntity record);

    /**
     * 查询按 API 维度的统计数据
     */
    MeteringStatAggregate queryStatByApi(String tenantId, Long apiAssetId, Date start, Date end);

    /**
     * 查询按应用维度的统计数据
     */
    MeteringStatAggregate queryStatByApp(String tenantId, Long appId, Date start, Date end);
}
