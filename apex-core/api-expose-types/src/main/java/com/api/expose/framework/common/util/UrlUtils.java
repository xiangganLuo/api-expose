package com.api.expose.framework.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * URL 处理工具类
 */
public class UrlUtils {

    /**
     * 拼接基础 URL 和相对路径
     * 自动处理斜杠，避免重复或缺失
     *
     * @param baseUrl      基础 URL（如：http://api.example.com）
     * @param relativePath 相对路径（如：/users/list）
     * @return 完整的 URL
     */
    public static String resolveUrl(String baseUrl, String relativePath) {
        if (StrUtil.isBlank(baseUrl)) {
            return relativePath;
        }
        
        if (StrUtil.isBlank(relativePath)) {
            return baseUrl;
        }
        
        boolean baseEndsWithSlash = baseUrl.endsWith("/");
        boolean pathStartsWithSlash = relativePath.startsWith("/");

        if (baseEndsWithSlash && pathStartsWithSlash) {
            // 两者都有斜杠，去掉一个
            return baseUrl + relativePath.substring(1);
        } else if (!baseEndsWithSlash && !pathStartsWithSlash) {
            // 两者都没有斜杠，添加一个
            return baseUrl + "/" + relativePath;
        } else {
            // 只有一个有斜杠，直接拼接
            return baseUrl + relativePath;
        }
    }
}
