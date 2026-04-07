package com.api.expose.domain.metering.service.impl;

import com.api.expose.domain.metering.adapter.repository.IApiCallRecordRepository;
import com.api.expose.domain.metering.service.IMetricsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 运营指标服务实现
 */
@Service
public class MetricsServiceImpl implements IMetricsService {

    @Resource
    private IApiCallRecordRepository apiCallRecordRepository;

    @Override
    public Map<String, Long> getTrafficTrend(int days) {
        return apiCallRecordRepository.queryTrafficTrend(days);
    }
}
