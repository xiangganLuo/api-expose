package com.api.expose.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/*
 * @author xiangganluo
 */

/**
 * 计量统计持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_metering_stat")
public class MeteringStatPO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 租户ID */
    private String tenantId;
    /** 统计维度 (API/APP/TENANT) */
    private String dimension;
    /** 维度值 */
    private String dimensionValue;
    /** 总调用次数 */
    private Long totalCalls;
    /** 成功次数 */
    private Long successCalls;
    /** 失败次数 */
    private Long failCalls;
    /** 平均延迟(ms) */
    private BigDecimal avgLatencyMs;
    /** 统计窗口开始时间 */
    private Date windowStart;
    /** 统计窗口结束时间 */
    private Date windowEnd;

}
