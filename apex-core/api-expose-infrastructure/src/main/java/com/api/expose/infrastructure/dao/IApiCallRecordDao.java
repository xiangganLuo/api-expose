package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.ApiCallRecordPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * API 调用记录 DAO
 */
@Mapper
public interface IApiCallRecordDao extends BaseMapper<ApiCallRecordPO> {

    /**
     * 聚合查询流量趋势
     */
    @Select("SELECT DATE_FORMAT(call_time, '%Y-%m-%d') as date, COUNT(*) as count " +
            "FROM apex_api_call_record " +
            "WHERE call_time > DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY date ORDER BY date")
    List<Map<String, Object>> queryTrafficTrend(@Param("days") int days);

    @Select("SELECT COUNT(*) as totalCalls, " +
            "SUM(CASE WHEN response_code < 400 THEN 1 ELSE 0 END) as successCalls, " +
            "SUM(CASE WHEN response_code >= 400 THEN 1 ELSE 0 END) as failCalls, " +
            "AVG(latency_ms) as avgLatencyMs " +
            "FROM apex_api_call_record " +
            "WHERE app_id = #{appId} AND call_time > DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    Map<String, Object> queryAppMetricsOverview(@Param("appId") Long appId, @Param("days") int days);

    @Select("SELECT DATE_FORMAT(call_time, '%Y-%m-%d') as date, " +
            "COUNT(*) as calls, " +
            "SUM(CASE WHEN response_code < 400 THEN 1 ELSE 0 END) as successCalls, " +
            "SUM(CASE WHEN response_code >= 400 THEN 1 ELSE 0 END) as failCalls, " +
            "AVG(latency_ms) as avgLatencyMs " +
            "FROM apex_api_call_record " +
            "WHERE app_id = #{appId} AND call_time > DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY date ORDER BY date")
    List<Map<String, Object>> queryAppMetricsTrend(@Param("appId") Long appId, @Param("days") int days);

    @Select("SELECT COUNT(*) as totalCalls, " +
            "SUM(CASE WHEN response_code < 400 THEN 1 ELSE 0 END) as successCalls, " +
            "SUM(CASE WHEN response_code >= 400 THEN 1 ELSE 0 END) as failCalls, " +
            "AVG(latency_ms) as avgLatencyMs " +
            "FROM apex_api_call_record " +
            "WHERE api_asset_id = #{apiAssetId} AND call_time > DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    Map<String, Object> queryAssetMetricsOverview(@Param("apiAssetId") Long apiAssetId, @Param("days") int days);

    @Select("SELECT DATE_FORMAT(call_time, '%Y-%m-%d') as date, " +
            "COUNT(*) as calls, " +
            "SUM(CASE WHEN response_code < 400 THEN 1 ELSE 0 END) as successCalls, " +
            "SUM(CASE WHEN response_code >= 400 THEN 1 ELSE 0 END) as failCalls, " +
            "AVG(latency_ms) as avgLatencyMs " +
            "FROM apex_api_call_record " +
            "WHERE api_asset_id = #{apiAssetId} AND call_time > DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY date ORDER BY date")
    List<Map<String, Object>> queryAssetMetricsTrend(@Param("apiAssetId") Long apiAssetId, @Param("days") int days);

}
