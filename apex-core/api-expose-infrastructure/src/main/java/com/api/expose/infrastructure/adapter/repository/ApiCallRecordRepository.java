package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.metering.adapter.repository.IApiCallRecordRepository;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;
import com.api.expose.infrastructure.dao.IApiCallRecordDao;
import com.api.expose.infrastructure.dao.po.ApiCallRecordPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, Object> queryAppMetricsOverview(Long appId, int days) {
        return apiCallRecordDao.queryAppMetricsOverview(appId, days);
    }

    @Override
    public List<Map<String, Object>> queryAppMetricsTrend(Long appId, int days) {
        return apiCallRecordDao.queryAppMetricsTrend(appId, days);
    }

    @Override
    public Map<String, Object> queryAssetMetricsOverview(Long apiAssetId, int days) {
        return apiCallRecordDao.queryAssetMetricsOverview(apiAssetId, days);
    }

    @Override
    public List<Map<String, Object>> queryAssetMetricsTrend(Long apiAssetId, int days) {
        return apiCallRecordDao.queryAssetMetricsTrend(apiAssetId, days);
    }

    @Override
    public List<ApiCallRecordEntity> queryRecordsByPeriod(Date startDate, Date endDate) {
        LambdaQueryWrapper<ApiCallRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ApiCallRecordPO::getCallTime, 
                startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
               .lt(ApiCallRecordPO::getCallTime,
                endDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        
        List<ApiCallRecordPO> pos = apiCallRecordDao.selectList(wrapper);
        return pos.stream().map(po -> ApiCallRecordEntity.builder()
                .tenantId(po.getTenantId())
                .apiAssetId(po.getApiAssetId())
                .appId(po.getAppId())
                .requestPath(po.getRequestPath())
                .httpMethod(po.getHttpMethod())
                .responseCode(po.getResponseCode())
                .latencyMs(po.getLatencyMs())
                .callerIp(po.getCallerIp())
                .callTime(po.getCallTime() != null ? 
                        Date.from(po.getCallTime().atZone(java.time.ZoneId.systemDefault()).toInstant()) : null)
                .build()).collect(Collectors.toList());
    }
}
