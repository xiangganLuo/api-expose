package com.api.expose.domain.metering.service.impl;

import com.api.expose.domain.metering.adapter.repository.IMeteringRepository;
import com.api.expose.domain.metering.model.aggregate.MeteringStatAggregate;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;
import com.api.expose.domain.metering.model.valobj.MeteringDimensionEnum;
import com.api.expose.domain.metering.service.IMeteringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 计量服务实现
 */
@Slf4j
@Service
public class MeteringServiceImpl implements IMeteringService {

    @Resource
    private IMeteringRepository meteringRepository;

    @Override
    public void recordApiCall(ApiCallRecordEntity record) {
        log.info("记录 API 调用, apiAssetId:{}, appId:{}, responseCode:{}", record.getApiAssetId(), record.getAppId(), record.getResponseCode());
        meteringRepository.saveCallRecord(record);
    }

    @Override
    public MeteringStatAggregate queryStatByApi(String tenantId, Long apiAssetId, Date start, Date end) {
        return meteringRepository.queryStat(tenantId, MeteringDimensionEnum.BY_API.getCode(), String.valueOf(apiAssetId), start, end);
    }

    @Override
    public MeteringStatAggregate queryStatByApp(String tenantId, Long appId, Date start, Date end) {
        return meteringRepository.queryStat(tenantId, MeteringDimensionEnum.BY_APP.getCode(), String.valueOf(appId), start, end);
    }
}
