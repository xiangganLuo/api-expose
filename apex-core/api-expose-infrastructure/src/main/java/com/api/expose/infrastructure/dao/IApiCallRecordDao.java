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
            "FROM api_call_record " +
            "WHERE call_time > DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY date ORDER BY date")
    List<Map<String, Object>> queryTrafficTrend(@Param("days") int days);

}
