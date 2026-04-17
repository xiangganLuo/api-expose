package com.api.expose.domain.metering.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricAggregationVO {

    private Long totalCalls;
    private Long successCalls;
    private Long failCalls;
    private Double avgLatencyMs;

    private List<Point> series;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private String time;
        private Long calls;
        private Long successCalls;
        private Long failCalls;
        private Double avgLatencyMs;
    }
}
