package com.myspringboot.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiehang
 * @date 2025/2/16 22:38
 *
 * 直接将过滤器注册到 Spring 的过滤器链中
 */
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<CorsFilter> loggingFilter() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorsFilter());
        registrationBean.addUrlPatterns("/*"); // 应用到所有 URL
        registrationBean.setOrder(1); // 设置过滤器优先级
        return registrationBean;
    }
}
