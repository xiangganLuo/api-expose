package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.metering.adapter.repository.IApiCallRecordRepository;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;
import com.api.expose.infrastructure.dao.IApiCallRecordDao;
import com.api.expose.infrastructure.dao.po.ApiCallRecordPO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用流水仓储实现
 */
@Repository
public class ApiCallRecordRepository implements IApiCallRecordRepository {

    @Resource
    private IApiCallRecordDao apiCallRecordDao;

    @Override
    public void saveRecord(ApiCallRecordEntity record) {
        ApiCallRecordPO po = ApiCallRecordPO.builder()
                .tenantId(record.getTenantId())
                .apiAssetId(record.getApiAssetId())
                .appId(record.getAppId())
                .requestPath(record.getRequestPath())
                .httpMethod(record.getHttpMethod())
                .responseCode(record.getResponseCode())
                .latencyMs(record.getLatencyMs())
                .callerIp(record.getCallerIp())
                .callTime(record.getCallTime() != null
                        ? record.getCallTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .build();
        apiCallRecordDao.insert(po);
    }

    @Override
    public Map<String, Long> queryTrafficTrend(int days) {
        List<Map<String, Object>> list = apiCallRecordDao.queryTrafficTrend(days);
        Map<String, Long> trend = new LinkedHashMap<>();
        for (Map<String, Object> map : list) {
            String date = map.get("date").toString();
            Long count = Long.valueOf(map.get("count").toString());
            trend.put(date, count);
        }
        return trend;
    }
}
