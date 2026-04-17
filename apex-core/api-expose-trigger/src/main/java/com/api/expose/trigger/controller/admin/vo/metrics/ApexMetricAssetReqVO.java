package com.api.expose.trigger.controller.admin.vo.metrics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.api.expose.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 监控指标(按资产) Request VO")
@Data
public class ApexMetricAssetReqVO {

    @Schema(description = "API 资产编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "apiAssetId 不能为空")
    private Long apiAssetId;

    @Schema(description = "查询天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "2022-02-02 00:00:00")
    @NotNull(message = "查询天数不能为空")
    private Integer days;

}
