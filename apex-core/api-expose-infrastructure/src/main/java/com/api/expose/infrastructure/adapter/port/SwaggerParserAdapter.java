package com.api.expose.infrastructure.adapter.port;

import com.api.expose.domain.api.adapter.port.IApiParserPort;
import com.api.expose.domain.api.model.entity.ApiEndpointEntity;
import com.api.expose.domain.api.model.valobj.ApiDefinitionVO;
import com.api.expose.domain.api.model.valobj.HttpMethodEnum;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Swagger/OpenAPI 解析适配器
 */
@Slf4j
@Component
public class SwaggerParserAdapter implements IApiParserPort {

    @Override
    public List<ApiEndpointEntity> parseOpenApi(String content) {
        log.info("开始解析 OpenAPI 内容, content 长度:{}", content.length());

        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        OpenAPI openAPI = parser.readContents(content).getOpenAPI();

        if (openAPI == null) {
            throw new RuntimeException("无法解析 OpenAPI 内容，请检查格式是否正确");
        }

        List<ApiEndpointEntity> endpoints = new ArrayList<>();
        Map<String, PathItem> paths = openAPI.getPaths();

        if (paths != null) {
            paths.forEach((path, pathItem) -> {
                // 遍历 HTTP 方法
                if (pathItem.getGet() != null) {
                    endpoints.add(buildEndpoint(path, HttpMethodEnum.GET, pathItem.getGet().getSummary()));
                }
                if (pathItem.getPost() != null) {
                    endpoints.add(buildEndpoint(path, HttpMethodEnum.POST, pathItem.getPost().getSummary()));
                }
                if (pathItem.getPut() != null) {
                    endpoints.add(buildEndpoint(path, HttpMethodEnum.PUT, pathItem.getPut().getSummary()));
                }
                if (pathItem.getDelete() != null) {
                    endpoints.add(buildEndpoint(path, HttpMethodEnum.DELETE, pathItem.getDelete().getSummary()));
                }
            });
        }

        return endpoints;
    }

    private ApiEndpointEntity buildEndpoint(String path, HttpMethodEnum method, String summary) {
        return ApiEndpointEntity.builder()
                .path(path)
                .httpMethod(method)
                .name(summary != null ? summary : path)
                .summary(summary)
                .definition(new ApiDefinitionVO()) // 这里可以细化解析请求响应参数
                .timeoutMs(3000)
                .build();
    }
}
