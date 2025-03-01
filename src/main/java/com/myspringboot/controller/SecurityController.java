package com.myspringboot.controller;

/**
 * @author xiehang
 * @create 2022-08-01 17:14
 */
public class SecurityController {

/*    protected void configure (HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                //使用spring表达式限定只有角色ROLE_USER或者ROLE_ADMIN
                .antMatchers("/user/**").access("hasRole('USER') or hasRole('ADMIN')")
                //设置访问权限给ROLE_ADMIN，要求是完整登录
                    .antMatchers("/admin/welcom").access("hasAnyAuthority('ROLE_ADMIN')" +
                "&&isFullyAuthenticated()")
                //限定"/admin/welcom2"访问权限给角色ROLE_ADMIN，允许不完全登陆
                .antMatchers("/admin/welcom2").access("hasAnyAuthority('ROLE_ADMIN')")
                //实用记住我的功能
                .and().rememberMe()
                //使用Spring Security默认的登录页面
                .and().formLogin()
                //启动HTTP基础验证
                .and().httpBasic();
    }*/
}
