package com.fuint.base.shiro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 设置拦截器,填充shiro user 信息
 * Created by hanxiaoqiang on 16/8/4.
 */
@Configuration
public class ShiroAdapter extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new ShiroInterceptor()).addPathPatterns("/**");
    }
}