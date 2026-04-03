package com.api.expose.domain.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 应用凭证实体 - API Key / Secret
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppCredentialEntity {

    /** 凭证ID */
    private Long credentialId;
    /** API Key */
    private String apiKey;
    /** API Secret */
    private String apiSecret;
    /** 创建时间 */
    private Date createTime;
    /** 过期时间 */
    private Date expireTime;
}
