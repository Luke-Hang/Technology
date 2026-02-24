package com.myspringboot.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xiehang
 * @date 2025/2/16 22:14
 */
@Component
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化逻辑（如果需要）
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 处理预检请求（OPTIONS 请求）
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK); // 返回 200 状态码
        }
        // 设置 CORS 相关的响应头
        // 允许所有源访问（生产环境建议指定具体域名）
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        // 允许的 HTTP 方法
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // 允许的自定义头
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization-Token," +
                "sessionToken,X-TOKEN");
        // 是否允许发送凭据
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        // 继续处理请求链
        chain.doFilter(request, response);
    }
    @Override
    public void destroy() {
        // 销毁逻辑（如果需要）
    }



}
