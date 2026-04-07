package com.api.expose.trigger.http;

import com.api.expose.api.dto.DeveloperAppDTO;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.service.IAppService;
import com.api.expose.types.common.Response;
import com.api.expose.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 开发者应用管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/apps")
public class AppController {

    @Resource
    private IAppService appService;

    /**
     * 创建应用
     */
    @PostMapping("")
    public Response<Boolean> createApp(@RequestBody DeveloperAppDTO requestDTO) {
        try {
            log.info("创建应用请求:{}", requestDTO.getAppName());
            DeveloperAppEntity appEntity = DeveloperAppEntity.builder()
                    .tenantId(requestDTO.getTenantId())
                    .appName(requestDTO.getAppName())
                    .description(requestDTO.getDescription())
                    .callbackUrl(requestDTO.getCallbackUrl())
                    .build();
            
            appService.createApp(appEntity);
            return Response.success(true);
        } catch (Exception e) {
            log.error("创建应用失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 查询应用列表
     */
    @GetMapping("")
    public Response<List<DeveloperAppDTO>> queryList(@RequestParam String tenantId) {
        try {
            List<DeveloperAppEntity> apps = appService.queryApps(tenantId);
            List<DeveloperAppDTO> dtos = apps.stream().map(app -> DeveloperAppDTO.builder()
                    .appId(app.getAppId())
                    .appName(app.getAppName())
                    .description(app.getDescription())
                    .apiKey(app.getApiKey())
                    .status(app.getStatus().getCode())
                    .build()).collect(Collectors.toList());
            
            return Response.success(dtos);
        } catch (Exception e) {
            log.error("查询应用列表失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }
}
