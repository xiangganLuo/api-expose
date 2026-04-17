package com.api.expose.trigger.controller.admin;

import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.tenant.core.aop.TenantIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.util.HashMap;
import java.util.Map;

import static com.api.expose.framework.common.pojo.CommonResult.success;

/*
 * @author xiangganluo
 */

@Tag(name = "管理后台 - APEx - 演示接口")
@RestController
@RequestMapping("/apex/demo")
@Validated
@PermitAll
public class ApexDemoController {

    @GetMapping("/get")
    @Operation(summary = "GET 演示接口")
    @TenantIgnore
    public CommonResult<Map<String, Object>> getDemo(@RequestParam(value = "name", defaultValue = "APEx") String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("method", "GET");
        result.put("name", name);
        result.put("timestamp", System.currentTimeMillis());
        return success(result);
    }

    @PostMapping("/post")
    @Operation(summary = "POST 演示接口")
    @TenantIgnore
    public CommonResult<Map<String, Object>> postDemo(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        result.put("method", "POST");
        result.put("receivedBody", body);
        result.put("timestamp", System.currentTimeMillis());
        return success(result);
    }

    @GetMapping("/headers")
    @Operation(summary = "Header 演示接口")
    @TenantIgnore
    public CommonResult<Map<String, String>> headerDemo(@RequestHeader Map<String, String> headers) {
        return success(headers);
    }

}
