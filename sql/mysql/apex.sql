-- 1. API 资产表
CREATE TABLE IF NOT EXISTS apex_api_asset (
     id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
     tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    name VARCHAR(128) NOT NULL COMMENT '资产名称',
    description VARCHAR(512) COMMENT '资产描述',
    group_name VARCHAR(64) COMMENT '分组名称',
    protocol_type VARCHAR(32) NOT NULL COMMENT '协议类型 (HTTP/HTTPS)',
    status VARCHAR(32) NOT NULL COMMENT '状态 (DRAFT/PUBLISHED/DEPRECATED/OFFLINE)',
    base_path VARCHAR(256) COMMENT '基础路径',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 资产主表';

-- 2. API 端点表
CREATE TABLE IF NOT EXISTS api_endpoint (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                            asset_id BIGINT NOT NULL COMMENT '所属资产ID',
                                            path VARCHAR(256) NOT NULL COMMENT '端点路径',
    http_method VARCHAR(32) NOT NULL COMMENT 'HTTP方法',
    name VARCHAR(128) COMMENT '端点名称',
    summary VARCHAR(256) COMMENT '精简描述',
    request_schema TEXT COMMENT '请求结构(JSON)',
    response_schema TEXT COMMENT '响应结构(JSON)',
    upstream_url VARCHAR(512) COMMENT '上游后端地址',
    timeout_ms INT DEFAULT 3000 COMMENT '超时时间(ms)',
    INDEX idx_asset_id (asset_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 端点表';

-- 3. API 版本表
CREATE TABLE IF NOT EXISTS apex_api_version (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                           asset_id BIGINT NOT NULL COMMENT '所属资产ID',
                                           version VARCHAR(32) NOT NULL COMMENT '版本号',
    active TINYINT DEFAULT 0 COMMENT '是否当前生效版本 (1-是, 0-否)',
    release_note TEXT COMMENT '发布日志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_asset_id (asset_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 版本表';

-- 4. 开发者应用表
CREATE TABLE IF NOT EXISTS apex_developer_app (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                             tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    app_name VARCHAR(128) NOT NULL COMMENT '应用名称',
    description VARCHAR(512) COMMENT '应用描述',
    status VARCHAR(32) NOT NULL COMMENT '状态 (ACTIVE/INACTIVE)',
    api_key VARCHAR(128) UNIQUE NOT NULL COMMENT 'API Key',
    api_secret VARCHAR(128) NOT NULL COMMENT 'API Secret',
    callback_url VARCHAR(256) COMMENT '回调地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开发者应用表';

-- 5. 订阅关系表
CREATE TABLE IF NOT EXISTS apex_subscription (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                            app_id BIGINT NOT NULL COMMENT '应用ID',
                                            api_asset_id BIGINT NOT NULL COMMENT 'API资产ID',
                                            status VARCHAR(32) NOT NULL COMMENT '订阅状态 (PENDING/APPROVED/REJECTED/REVOKED)',
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    approve_time DATETIME COMMENT '通过时间',
    remark VARCHAR(256) COMMENT '备注',
    INDEX idx_app_api (app_id, api_asset_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 订阅关系表';

-- 6. 治理策略表
CREATE TABLE IF NOT EXISTS governance_policy (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                                 policy_name VARCHAR(128) NOT NULL COMMENT '策略名称',
    scope VARCHAR(32) NOT NULL COMMENT '生效范围 (GLOBAL/API_LEVEL/APP_LEVEL)',
    api_asset_id BIGINT COMMENT '关联API资产ID',
    app_id BIGINT COMMENT '关联应用ID',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用 (1-是, 0-否)',
    rate_limit_json TEXT COMMENT '限流规则JSON',
    circuit_breaker_json TEXT COMMENT '熔断规则JSON',
    access_control_json TEXT COMMENT '访问控制规则JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 治理策略表';

-- 7. 网关路由规则表
CREATE TABLE IF NOT EXISTS apex_route_rule (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                          api_asset_id BIGINT NOT NULL COMMENT 'API资产ID',
                                          endpoint_id BIGINT NOT NULL COMMENT '端点ID',
                                          http_method VARCHAR(32) NOT NULL COMMENT '请求方法',
    gateway_path VARCHAR(256) NOT NULL COMMENT '网关路径',
    upstream_path VARCHAR(256) NOT NULL COMMENT '上游路径',
    upstream_url VARCHAR(512) NOT NULL COMMENT '上游真实地址',
    status VARCHAR(32) NOT NULL COMMENT '状态 (ACTIVE/INACTIVE)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_gateway_path (gateway_path)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网关路由规则表';

-- 8. API 调用记录表
CREATE TABLE IF NOT EXISTS apex_api_call_record (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                               tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    api_asset_id BIGINT NOT NULL COMMENT 'API资产ID',
    app_id BIGINT NOT NULL COMMENT '应用ID',
    request_path VARCHAR(512) NOT NULL COMMENT '请求完整路径',
    http_method VARCHAR(32) NOT NULL COMMENT '请求方法',
    response_code INT COMMENT '响应状态码',
    latency_ms BIGINT COMMENT '响应耗时(ms)',
    call_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
    caller_ip VARCHAR(64) COMMENT '调用者IP',
    INDEX idx_call_time (call_time),
    INDEX idx_tenant_app (tenant_id, app_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 调用明细记录表';
-- 10. 账单表
CREATE TABLE IF NOT EXISTS billing_bill (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                            tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    app_id BIGINT NOT NULL COMMENT '应用ID',
    billing_month VARCHAR(7) NOT NULL COMMENT '账单月份 (YYYY-MM)',
    total_calls BIGINT DEFAULT 0 COMMENT '总调用次数',
    total_amount DECIMAL(18, 4) DEFAULT 0.0000 COMMENT '总金额',
    status VARCHAR(32) NOT NULL COMMENT '状态 (UNPAID/PAID/PARTIAL)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX uk_tenant_app_month (tenant_id, app_id, billing_month)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单结算表';

-- 11. 计量统计表 (用于按维度聚合存储)
CREATE TABLE IF NOT EXISTS apex_metering_stat (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                             tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    dimension VARCHAR(32) NOT NULL COMMENT '统计维度 (API/APP/TENANT)',
    dimension_value VARCHAR(128) NOT NULL COMMENT '维度值',
    total_calls BIGINT DEFAULT 0 COMMENT '总调用次数',
    success_calls BIGINT DEFAULT 0 COMMENT '成功次数',
    fail_calls BIGINT DEFAULT 0 COMMENT '失败次数',
    avg_latency_ms DECIMAL(18, 4) DEFAULT 0.0000 COMMENT '平均延迟(ms)',
    window_start DATETIME NOT NULL COMMENT '统计窗口开始时间',
    window_end DATETIME NOT NULL COMMENT '统计窗口结束时间',
    INDEX idx_tenant_window (tenant_id, window_start, window_end),
    UNIQUE INDEX uk_tenant_dim_val_window (tenant_id, dimension, dimension_value, window_start, window_end)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量统计表';
