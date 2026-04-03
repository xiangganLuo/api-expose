package com.api.expose.domain.api.adapter.port;

import com.api.expose.domain.api.model.entity.ApiEndpointEntity;

import java.util.List;

/**
 * API 解析端口接口 - 对接外部 Swagger/OpenAPI 解析能力
 */
public interface IApiParserPort {

    /**
     * 解析 Swagger/OpenAPI 文件内容，返回端点列表
     */
    List<ApiEndpointEntity> parseOpenApi(String content);
}
