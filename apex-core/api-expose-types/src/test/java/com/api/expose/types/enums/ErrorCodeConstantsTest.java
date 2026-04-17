package com.api.expose.types.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ErrorCodeConstants} 的单元测试
 */
public class ErrorCodeConstantsTest {

    @Test
    public void testForbiddenErrorCode() {
        // 验证 forbidden 错误码的属性
        assertNotNull(ErrorCodeConstants.forbidden, "forbidden 错误码不应为 null");
        
        // 验证错误码值
        assertEquals(1_003_000_001, ErrorCodeConstants.forbidden.getCode(), 
                "错误码应为 1003000001");
        
        // 验证错误消息
        assertEquals("当前应用未订阅该 API 资产，或订阅尚未审核通过", 
                ErrorCodeConstants.forbidden.getMsg(), 
                "错误消息应匹配");
    }

    @Test
    public void testErrorCodeFormat() {
        // 验证错误码格式符合 infra 系统 1-003-000-000 段的规范
        Integer code = ErrorCodeConstants.forbidden.getCode();
        assertTrue(code >= 1_003_000_000 && code < 1_004_000_000, 
                "错误码应在 1-003-000-000 段范围内");
    }

    @Test
    public void testErrorMessageNotEmpty() {
        // 验证错误消息不为空
        String msg = ErrorCodeConstants.forbidden.getMsg();
        assertNotNull(msg, "错误消息不应为 null");
        assertFalse(msg.isEmpty(), "错误消息不应为空字符串");
    }
}
