package com.cashgenerator.interceptor;

import com.cashgenerator.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        //没有 token 或者 token 格式不正确
        if (StringUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);//继续过滤连，但未认证
            return;
        }


        ///  nihao

        //验证 token 有效性
        String token = header.split(" ")[1].trim();
        if (!jwtUtils.validateJwtToken(token)) {
            // token 无效
            filterChain.doFilter(request, response);//继续过滤连，但未认证
            return;
        }

    }
}
