package com.cashgenerator.config;

import com.cashgenerator.interceptor.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

/**
 * 3. Spring Security配置 (WebSecurityConfig)
 * **配置要点**:
 * - **无状态会话**: `SessionCreationPolicy.STATELESS` - 不使用Session，完全依赖JWT
 * - **CSRF禁用**: 因为使用JWT，不需要CSRF保护
 * - **过滤器链**: JwtTokenFilter在`UsernamePasswordAuthenticationFilter`之后执行
 * - **公开端点**: 以下端点不需要JWT认证：
 *     - Swagger相关端点 (`/swagger-ui/**`, `/api-docs/**`)
 *     - Actuator端点 (`/actuator/**`)
 *     - 登录相关端点 (`/authenticate/**`, `/customer/api/v1/validateCustomerCredentials`)
 *     - 其他公开API
 *
 *4. Spring Security
 *    - 检查SecurityContext中的认证信息
 *    - 决定是否允许访问
 *
 *
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * 5. Spring Security授权检查
     *
     * **执行位置**: `WebSecurityConfig`的授权规则
     *    - 检查SecurityContext中的认证信息
     *    - 决定是否允许访问
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
     */

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.
                cors().configurationSource(CorsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((authorize) -> authorize.requestMatchers(
                                        "/swagger-ui/**",
                                        "/api-docs/**",
                                        "/actuator/**",
                                        "/authenticate/**"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    public CorsConfigurationSource CorsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Arrays.asList("*"));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
            config.setAllowedHeaders(Arrays.asList("*"));
            return config;
        };
    }
}
