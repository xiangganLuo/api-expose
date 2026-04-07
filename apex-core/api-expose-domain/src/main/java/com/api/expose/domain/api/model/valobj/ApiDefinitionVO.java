package com.api.expose.domain.api.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * API 定义值对象 - 内部标准化的 API 解析模型
 * 包含请求参数结构、响应结构、认证方式等
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiDefinitionVO {

    /** 请求参数模型（JSON Schema） */
    private String requestSchema;
    /** 响应结构模型（JSON Schema） */
    private String responseSchema;
    /** 请求头定义 */
    private Map<String, String> headers;
    /** 查询参数列表 */
    private List<String> queryParams;
    /** 请求内容类型 */
    private String contentType;
    /** API 描述 */
    private String description;
}
