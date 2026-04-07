package com.api.expose.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * @author xiangganluo
 */

/**
 * API 调用流水记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_api_call_record")
public class ApiCallRecordPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String tenantId;
    private Long apiAssetId;
    private Long appId;
    private String requestPath;
    private String httpMethod;
    private Integer responseCode;
    private Long latencyMs;
    private String callerIp;
    private Date callTime;

}
