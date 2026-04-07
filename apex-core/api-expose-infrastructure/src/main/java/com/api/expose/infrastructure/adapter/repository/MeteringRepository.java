package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.metering.adapter.repository.IMeteringRepository;
import com.api.expose.domain.metering.model.aggregate.MeteringStatAggregate;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;
import com.api.expose.domain.metering.model.valobj.MeteringDimensionEnum;
import com.api.expose.infrastructure.dao.IApiCallRecordDao;
import com.api.expose.infrastructure.dao.IMeteringStatDao;
import com.api.expose.infrastructure.dao.po.ApiCallRecordPO;
import com.api.expose.infrastructure.dao.po.MeteringStatPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 计量仓储实现
 */
@Repository
public class MeteringRepository implements IMeteringRepository {

    @Resource
    private IApiCallRecordDao apiCallRecordDao;

    @Resource
    private IMeteringStatDao meteringStatDao;

    @Override
    public void saveCallRecord(ApiCallRecordEntity record) {
        ApiCallRecordPO po = ApiCallRecordPO.builder()
                .tenantId(record.getTenantId())
                .apiAssetId(record.getApiAssetId())
                .appId(record.getAppId())
                .requestPath(record.getRequestPath())
                .httpMethod(record.getHttpMethod())
                .responseCode(record.getResponseCode())
                .latencyMs(record.getLatencyMs())
                .callerIp(record.getCallerIp())
                .callTime(record.getCallTime())
                .build();
        apiCallRecordDao.insert(po);
    }

    @Override
    public void batchSaveCallRecords(List<ApiCallRecordEntity> records) {
        // 使用 MyBatis Plus IService 可以更好的支持批量，这里简单实现
        for (ApiCallRecordEntity record : records) {
            saveCallRecord(record);
        }
    }

    @Override
    public MeteringStatAggregate queryStat(String tenantId, String dimension, String dimensionValue, Date start, Date end) {
        LambdaQueryWrapper<MeteringStatPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeteringStatPO::getTenantId, tenantId)
                .eq(MeteringStatPO::getDimension, dimension)
                .eq(MeteringStatPO::getDimensionValue, dimensionValue)
                .eq(MeteringStatPO::getWindowStart, start)
                .eq(MeteringStatPO::getWindowEnd, end);
        
        MeteringStatPO po = meteringStatDao.selectOne(wrapper);
        if (po == null) {
            return null;
        }

        return MeteringStatAggregate.builder()
                .statId(po.getId())
                .tenantId(po.getTenantId())
                .dimension(MeteringDimensionEnum.valueOf(po.getDimension()))
                .dimensionValue(po.getDimensionValue())
                .totalCalls(po.getTotalCalls())
                .successCalls(po.getSuccessCalls())
                .failCalls(po.getFailCalls())
                .avgLatencyMs(po.getAvgLatencyMs())
                .windowStart(po.getWindowStart())
                .windowEnd(po.getWindowEnd())
                .build();
    }

    @Override
    public void saveStat(MeteringStatAggregate stat) {
        MeteringStatPO po = MeteringStatPO.builder()
                .id(stat.getStatId())
                .tenantId(stat.getTenantId())
                .dimension(stat.getDimension().name())
                .dimensionValue(stat.getDimensionValue())
                .totalCalls(stat.getTotalCalls())
                .successCalls(stat.getSuccessCalls())
                .failCalls(stat.getFailCalls())
                .avgLatencyMs(stat.getAvgLatencyMs())
                .windowStart(stat.getWindowStart())
                .windowEnd(stat.getWindowEnd())
                .build();
        
        if (po.getId() != null) {
            meteringStatDao.updateById(po);
        } else {
            // 先尝试查找是否存在
            LambdaQueryWrapper<MeteringStatPO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MeteringStatPO::getTenantId, po.getTenantId())
                    .eq(MeteringStatPO::getDimension, po.getDimension())
                    .eq(MeteringStatPO::getDimensionValue, po.getDimensionValue())
                    .eq(MeteringStatPO::getWindowStart, po.getWindowStart())
                    .eq(MeteringStatPO::getWindowEnd, po.getWindowEnd());
            
            MeteringStatPO existing = meteringStatDao.selectOne(wrapper);
            if (existing != null) {
                po.setId(existing.getId());
                meteringStatDao.updateById(po);
            } else {
                meteringStatDao.insert(po);
            }
        }
    }
}
