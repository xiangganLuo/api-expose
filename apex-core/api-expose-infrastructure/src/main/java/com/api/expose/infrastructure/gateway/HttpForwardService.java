package com.api.expose.infrastructure.gateway;

import com.api.expose.domain.gateway.adapter.port.IForwardPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 基础 HTTP 转发服务实现 (WebClient 非阻塞版)
 */
@Slf4j
@Service
public class HttpForwardService implements IForwardPort {

    private final WebClient webClient;

    public HttpForwardService() {
        // 初始配置连接池、超时等 (后续可在配置类定制)
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 限制 10MB
                .build();
    }

    @Override
    public Mono<ResponseEntity<byte[]>> forward(String url, HttpMethod method, Map<String, String> headers, Object body) {
        log.debug("开始网关转发: {} [{}]", url, method);

        WebClient.RequestBodySpec request = webClient.method(method)
                .uri(url);

        // 设置 Header
        if (headers != null) {
            headers.forEach(request::header);
        }

        // 设置 Body
        if (body != null) {
            request.bodyValue(body);
        }

        return request.retrieve()
                .toEntity(byte[].class)
                .doOnError(e -> log.error("网关转发异常: {}", url, e));
    }
}
