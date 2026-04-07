package com.api.expose.domain.metering.adapter.repository;

import com.api.expose.domain.metering.model.aggregate.MeteringStatAggregate;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;

import java.util.Date;
import java.util.List;

/**
 * 计量仓储接口
 */
public interface IMeteringRepository {

    /**
     * 保存调用记录
     */
    void saveCallRecord(ApiCallRecordEntity record);

    /**
     * 批量保存调用记录
     */
    void batchSaveCallRecords(List<ApiCallRecordEntity> records);

    /**
     * 查询统计数据
     */
    MeteringStatAggregate queryStat(String tenantId, String dimension, String dimensionValue, Date start, Date end);

    /**
     * 保存统计数据
     */
    void saveStat(MeteringStatAggregate stat);
}
