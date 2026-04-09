package com.api.expose.types.framework.web.config;

import com.api.expose.framework.swagger.config.SwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * core 模块的 web 组件的 Configuration
 *
 * @author apex
 */
@Configuration(proxyBeanMethods = false)
public class CoreWebConfiguration {

    /**
     * system 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi systemGroupedOpenApi() {
        return SwaggerAutoConfiguration.buildGroupedOpenApi("core");
    }

}
