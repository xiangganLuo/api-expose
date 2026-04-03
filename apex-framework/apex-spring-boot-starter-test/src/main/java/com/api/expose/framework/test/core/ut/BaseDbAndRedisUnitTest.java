package com.api.expose.framework.test.core.ut;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.api.expose.framework.datasource.config.DataSourceAutoConfiguration;
import com.api.expose.framework.mybatis.config.MybatisAutoConfiguration;
import com.api.expose.framework.redis.config.RedisAutoConfiguration;
import com.api.expose.framework.test.config.RedisTestConfiguration;
import com.api.expose.framework.test.config.SqlInitializationTestConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

/**
 * 依赖内存 DB + Redis 的单元测试
 *
 * 相比 {@link BaseDbUnitTest} 来说，额外增加了内存 Redis
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseDbAndRedisUnitTest.Application.class)
@ActiveProfiles("unit-test") // 设置使用 application-unit-test 配置文件
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // 每个单元测试结束后，清理 DB
public class BaseDbAndRedisUnitTest {

    @Import({
            // DB 配置类
            DataSourceAutoConfiguration.class, // 自己的 DB 配置类
            DataSourceAutoConfiguration.class, // Spring DB 自动配置类
            DataSourceTransactionManagerAutoConfiguration.class, // Spring 事务自动配置类
            DruidDataSourceAutoConfigure.class, // Druid 自动配置类
            SqlInitializationTestConfiguration.class, // SQL 初始化
            // MyBatis 配置类
            MybatisAutoConfiguration.class, // 自己的 MyBatis 配置类
            MybatisPlusAutoConfiguration.class, // MyBatis 的自动配置类

            // Redis 配置类
            RedisTestConfiguration.class, // Redis 测试配置类，用于启动 RedisServer
            RedisAutoConfiguration.class, // 自己的 Redis 配置类
            RedisAutoConfiguration.class, // Spring Redis 自动配置类
            RedissonAutoConfiguration.class, // Redisson 自动配置类

            // 其它配置类
            SpringUtil.class
    })
    public static class Application {
    }

}
