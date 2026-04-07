package com.api.expose.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import com.api.expose.framework.common.util.servlet.ServletUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Request Body 缓存 Filter，实现它的可重复读取
 *
 */
public class CacheRequestBodyFilter extends OncePerRequestFilter {

    /**
     * 需要排除的 URI
     */
    private static final String[] IGNORE_URIS = {"/admin/", "/actuator/"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(new CacheRequestBodyWrapper(request), response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 1. 校验是否为排除的 URL
        String requestURI = request.getRequestURI();
        if (StrUtil.startWithAny(requestURI, IGNORE_URIS)) {
            return true;
        }

        // 2. 只处理 json 请求内容
        return !ServletUtils.isJsonRequest(request);
    }

}
