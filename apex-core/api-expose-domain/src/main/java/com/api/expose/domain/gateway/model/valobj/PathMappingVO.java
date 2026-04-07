package com.api.expose.domain.gateway.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 路径映射值对象 - 网关外部路径到内部上游路径的映射
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathMappingVO {

    /** 网关对外路径 (如 /open/v1/users) */
    private String gatewayPath;
    /** 上游内部路径 (如 /api/users) */
    private String upstreamPath;
    /** 是否启用路径重写 */
    private Boolean rewriteEnabled;
    /** 路径重写正则 */
    private String rewriteRegex;
    /** 路径重写替换 */
    private String rewriteReplacement;
}
