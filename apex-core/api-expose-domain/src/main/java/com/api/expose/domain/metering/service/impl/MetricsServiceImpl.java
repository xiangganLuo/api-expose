package com.api.expose.domain.metering.service.impl;

import com.api.expose.domain.metering.adapter.repository.IApiCallRecordRepository;
import com.api.expose.domain.metering.model.valobj.MetricAggregationVO;
import com.api.expose.domain.metering.service.IMetricsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public MetricAggregationVO getAppMetrics(Long appId, int days) {
        Map<String, Object> overview = apiCallRecordRepository.queryAppMetricsOverview(appId, days);
        List<Map<String, Object>> trend = apiCallRecordRepository.queryAppMetricsTrend(appId, days);
        return buildAggregation(overview, trend);
    }

    @Override
    public MetricAggregationVO getAssetMetrics(Long apiAssetId, int days) {
        Map<String, Object> overview = apiCallRecordRepository.queryAssetMetricsOverview(apiAssetId, days);
        List<Map<String, Object>> trend = apiCallRecordRepository.queryAssetMetricsTrend(apiAssetId, days);
        return buildAggregation(overview, trend);
    }

    private MetricAggregationVO buildAggregation(Map<String, Object> overview, List<Map<String, Object>> trend) {
        MetricAggregationVO vo = new MetricAggregationVO();
        vo.setTotalCalls(overview != null && overview.get("totalCalls") != null ? Long.valueOf(overview.get("totalCalls").toString()) : 0L);
        vo.setSuccessCalls(overview != null && overview.get("successCalls") != null ? Long.valueOf(overview.get("successCalls").toString()) : 0L);
        vo.setFailCalls(overview != null && overview.get("failCalls") != null ? Long.valueOf(overview.get("failCalls").toString()) : 0L);
        vo.setAvgLatencyMs(overview != null && overview.get("avgLatencyMs") != null ? Double.valueOf(overview.get("avgLatencyMs").toString()) : 0.0);

        List<MetricAggregationVO.Point> series = new ArrayList();
        if (trend != null) {
            for (Map<String, Object> map : trend) {
                MetricAggregationVO.Point point = new MetricAggregationVO.Point();
                point.setTime(map.get("date") != null ? map.get("date").toString() : "");
                point.setCalls(map.get("calls") != null ? Long.valueOf(map.get("calls").toString()) : 0L);
                point.setSuccessCalls(map.get("successCalls") != null ? Long.valueOf(map.get("successCalls").toString()) : 0L);
                point.setFailCalls(map.get("failCalls") != null ? Long.valueOf(map.get("failCalls").toString()) : 0L);
                point.setAvgLatencyMs(map.get("avgLatencyMs") != null ? Double.valueOf(map.get("avgLatencyMs").toString()) : 0.0);
                series.add(point);
            }
        }
        vo.setSeries(series);
        return vo;
    }
}
