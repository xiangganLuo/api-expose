package com.api.expose.trigger.controller.open;

import com.api.expose.domain.gateway.service.IGatewayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/*
 * @author xiangganluo
 */

/**
 * 统一网关业务代理入口
 * 拦截 /open/** 路径
 */
@Slf4j
@Tag(name = "APEx - 网关代理")
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
    @PermitAll
    public Mono<ResponseEntity<byte[]>> proxy(
            @RequestHeader(value = "X-API-KEY", required = false) String headerKey,
            @RequestParam(value = "apiKey", required = false) String queryKey,
            @RequestBody(required = false) byte[] body,
            HttpServletRequest request) {

        String apiKey = (headerKey != null) ? headerKey : queryKey;
        String path = request.getRequestURI().substring(5);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }

        return gatewayService.execute(apiKey, path, method, headers, body);
    }
}
