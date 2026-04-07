package com.api.expose.trigger.http;

import com.api.expose.domain.gateway.service.IGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一网关业务代理入口
 * 拦截 /open/** 路径
 */
@Slf4j
@RestController
@RequestMapping("/open")
public class GatewayProxyController {

    @Resource
    private IGatewayService gatewayService;

    /**
     * 核心网关代理
     * 路由规则: /open/{gatewayPath} -> upstream_url + upstream_path
     * 鉴权: 必须在 Header 包含 X-API-KEY 或在参数中包含 apiKey
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<byte[]>> proxy(
            @RequestHeader(value = "X-API-KEY", required = false) String headerKey,
            @RequestParam(value = "apiKey", required = false) String queryKey,
            @RequestBody(required = false) byte[] body,
            HttpServletRequest request) {

        // 1. 尝试获取 ApiKey
        String apiKey = (headerKey != null) ? headerKey : queryKey;
        
        // 2. 获取实际请求路径 (去除 /open 前缀)
        String path = request.getRequestURI().substring(5); // substring("/open".length())
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        // 3. 克隆 Header (透传至上游)
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }

        // 4. 执行网关中央处理逻辑
        return gatewayService.execute(apiKey, path, method, headers, body);
    }
}
