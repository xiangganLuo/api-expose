package com.api.expose.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${apex.info.base-package}
@SpringBootApplication(scanBasePackages = {"${apex.info.base-package}.server"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
