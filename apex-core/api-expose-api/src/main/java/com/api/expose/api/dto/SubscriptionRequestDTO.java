package com.api.expose.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订阅请求 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 应用ID */
    private Long appId;
    /** API 资产ID */
    private Long apiAssetId;
    /** 备注 */
    private String remark;
}
