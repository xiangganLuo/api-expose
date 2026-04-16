package com.api.expose.trigger.controller.admin.vo.metrics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 监控指标 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApexMetricRespVO {

    @Schema(description = "总调用次数", example = "1024")
    private Long totalCalls;

    @Schema(description = "成功调用次数", example = "1000")
    private Long successCalls;

    @Schema(description = "失败调用次数", example = "24")
    private Long failCalls;

    @Schema(description = "平均延迟(ms)", example = "32.5")
    private Double avgLatencyMs;

    @Schema(description = "时间序列")
    private List<MetricPoint> series;

    @Schema(description = "单个指标点")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricPoint {

        @Schema(description = "时间标签", example = "2026-04-16")
        private String time;

        @Schema(description = "调用次数", example = "128")
        private Long calls;

        @Schema(description = "成功次数", example = "120")
        private Long successCalls;

        @Schema(description = "失败次数", example = "8")
        private Long failCalls;

        @Schema(description = "平均延迟(ms)", example = "28.0")
        private Double avgLatencyMs;

    }

}
