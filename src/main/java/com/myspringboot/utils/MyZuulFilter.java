package com.myspringboot.utils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiehang
 * @create 2022-08-13 10:57
 */
@Component
public class MyZuulFilter extends ZuulFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //是否过滤
    @Override
    public boolean shouldFilter(){
        //获取当前请求上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取当前请求request
        HttpServletRequest request = context.getRequest();
        //取出表单序列号
        String seriaNum = request.getParameter("seriaNum");
        //如果验证码存在，返回true，启用过滤器
        return StringUtils.isEmpty(seriaNum);
    }

    //过滤器逻辑方法
    @Override
    public Object run(){
        //获取当前请求上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取当前请求request
        HttpServletRequest request = context.getRequest();
        //获取请求参数
        String seriaNum = request.getParameter("seriaNum");
        String verificationCode = request.getParameter("verificationCode");

        //从redis中获取请求参数
        String verifiCode = stringRedisTemplate.opsForValue().get("verificationCode");
        //redis验证码为空或者与请求不一致，拦截请求报出错误
        if (StringUtils.isBlank(verifiCode)||verificationCode.equals(verifiCode)){
            //不再转发请求
            context.setSendZuulResponse(false);
            //设置http 响应码微401(未授权状态码)
            context.setResponseStatusCode(401);
            //设置响应类型为json数据
            context.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8.getType());
            //设置响应体
            context.setResponseBody("{'sucess':false,'message':verificationCode error}");
        }
        //当redis验证码与请求体RequestContext中的验证码一致时，不报错，允许该请求通过过滤器
        return null;
    }

    //过滤器类型为请求前
    @Override
    public String filterType() {
        return "pre";
    }

    //过滤器排序，数字越小优先级越高
    @Override
    public int filterOrder() {
        return 0;
    }
}
