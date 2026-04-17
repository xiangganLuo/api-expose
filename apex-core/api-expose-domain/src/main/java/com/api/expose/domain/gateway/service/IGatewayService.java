package com.api.expose.domain.gateway.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 核心网关代理服务
 */
public interface IGatewayService {

    /**
     * 网关核心转发逻辑 (包含鉴权、路由、治理、审计)
     * @param apiKey 应用密钥
     * @param path 请求路径
     * @param queryString 请求查询参数
     * @param method HTTP方法
     * @param headers 请求头
     * @param body 请求体
     * @return 响应结果
     */
    Mono<ResponseEntity<byte[]>> execute(String apiKey, String path, String queryString, HttpMethod method, Map<String, String> headers, Object body);

}
