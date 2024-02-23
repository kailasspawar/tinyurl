package com.convert.tinyurl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.convert.tinyurl.interceptor.JwtTokenInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/tinyurl/v1/**", "/chatgpt/v1/**")
                .excludePathPatterns("/tinyurl/v1/login", "/tinyurl/v1/hello");// Add the appropriate URL patterns to apply the interceptor
    }
}
