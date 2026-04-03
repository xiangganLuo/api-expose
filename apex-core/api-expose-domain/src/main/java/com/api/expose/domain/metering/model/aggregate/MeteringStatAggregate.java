package com.api.expose.domain.metering.model.aggregate;

import com.api.expose.domain.metering.model.valobj.MeteringDimensionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 计量统计聚合根 - 按维度聚合的调用统计数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeteringStatAggregate {

    /** 统计ID */
    private Long statId;
    /** 租户ID */
    private String tenantId;
    /** 统计维度 */
    private MeteringDimensionEnum dimension;
    /** 维度值（apiAssetId / appId / tenantId） */
    private String dimensionValue;
    /** 总调用次数 */
    private Long totalCalls;
    /** 成功次数 */
    private Long successCalls;
    /** 失败次数 */
    private Long failCalls;
    /** 平均延迟(毫秒) */
    private BigDecimal avgLatencyMs;
    /** 统计时间窗口开始 */
    private Date windowStart;
    /** 统计时间窗口结束 */
    private Date windowEnd;

    /**
     * 计算成功率
     */
    public BigDecimal getSuccessRate() {
        if (totalCalls == null || totalCalls == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(successCalls)
                .divide(BigDecimal.valueOf(totalCalls), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
