package com.cashgenerator.interceptor;

import com.cashgenerator.model.LoginBo;
import com.cashgenerator.model.LoginUserBo;
import com.cashgenerator.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * JWT过滤器 (JwtTokenFilter)
 * **功能**:
 * - 拦截所有HTTP请求
 * - 从`Authorization`请求头中提取JWT Token（格式：`Bearer <token>`）
 * - 验证Token有效性
 * - 从Token中提取用户信息并设置到Spring Security上下文
 *
 * **工作流程**:
 * ```java
 * 1. 检查Authorization请求头是否存在且以"Bearer "开头
 * 2. 提取Token字符串
 * 3. 调用jwtUtils.validateJwtToken()验证Token
 * 4. 从Token中提取两个Claim：
 *    - "customer_auth_token" -> LoginBo对象（用户登录信息）
 *    - "app_auth_token" -> 应用名称字符串
 * 5. 根据提取的信息创建UserDetails对象
 * 6. 设置到SecurityContextHolder中，供后续请求使用
 *
 * JwtTokenFilter
 *    - 检查Authorization头格式
 *    - 提取Token
 *    - 验证Token有效性
 *    - 提取用户信息
 *    - 设置SecurityContext
 *
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    //检查Authorization头
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 1.提取Token
        //检查Authorization头格式,没有 token 或者 token 格式不正确
        if (StringUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);//继续过滤连，但未认证
            return;// Spring Security会在后续拒绝请求
        }


        // 2.验证Token
        //提取Token, 验证 token 有效性
        // 提取Token,
        // 从 "Bearer <token>" 中提取token部分 header.split(" ")[0] 为Bearer
        // header.split(" ")[1] 为 token
        String token = header.split(" ")[1].trim();
        if (!jwtUtils.validateJwtToken(token)) {
            // Token无效（过期、签名错误等）,验证签名和过期时间
            chain.doFilter(request, response);//继续过滤连，但未认证
            return;  // Spring Security会在后续拒绝请求
        }


        // 3.提取用户信息
        // getClaim() → 提取用户信息
        // 从Token中提取customer_auth_token Claim
        LoginBo loginBo = jwtUtils.getClaim(token, "customer_auth_token", LoginBo.class);

        // 从Token中提取app_auth_token Claim
        String applicationName = jwtUtils.getClaim(token, "app_auth_token", String.class);

        /**
         * **注意**:
         * - 如果Token是`appAuthToken`，`loginBo`可能为null
         * - 如果Token是`customerAuthToken`，`loginBo`包含完整用户信息
         */


        //创建UserDetails
        UserDetails userDetails = null;
        if(loginBo != null) {
            // 有用户登录信息，创建用户认证对象
            userDetails = new LoginUserBo(1L, loginBo.getLoginId());
        } else {
            // 只有应用Token，创建应用认证对象
            new LoginUserBo(-1L, applicationName);
        }

        //设置Spring Security认证上下文
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,           // 主体
                        null,                  // 凭证（JWT不需要）
                        userDetails == null ?
                                List.of() : userDetails.getAuthorities()  // 权限列表
                );

        // 设置认证详情
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        //设置SecurityContext
        // 设置 authentication 到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

//        **关键**: 此时请求已被标记为"已认证"，Spring Security会允许请求继续。
        chain.doFilter(request, response);

//        5. Spring Security授权检查 详见 WebSecurityConfig.java
        /**
         * **执行位置**: `WebSecurityConfig`的授权规则
         *
         * **检查**:
         * ```java
         * .authorizeHttpRequests((authorize) -> authorize
         *     .requestMatchers(...公开端点...)
         *     .permitAll()
         *     .anyRequest()
         *     .authenticated()  // 检查SecurityContext是否有认证信息
         * )
         *
         * **结果**:
         * - ✅ 如果`SecurityContext`中有认证信息 → 允许访问
         * - ❌ 如果没有认证信息 → 返回401 Unauthorized
         *
         */
    }
}
