package com.api.expose.domain.gateway.adapter.port;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 转发端口接口
 */
public interface IForwardPort {

    /**
     * 发起 HTTP 转发
     * @param url 目标 URL
     * @param method HTTP 方法
     * @param headers 请求头
     * @param body 请求体
     * @return 响应字节流
     */
    Mono<ResponseEntity<byte[]>> forward(String url, HttpMethod method, Map<String, String> headers, Object body);
}
