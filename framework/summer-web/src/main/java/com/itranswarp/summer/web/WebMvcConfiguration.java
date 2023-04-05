package com.itranswarp.summer.web;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.itranswarp.summer.annotation.Autowired;
import com.itranswarp.summer.annotation.Bean;
import com.itranswarp.summer.annotation.Configuration;
import com.itranswarp.summer.annotation.Value;

import jakarta.servlet.ServletContext;

@Configuration
public class WebMvcConfiguration {

    @Bean(initMethod = "init")
    ViewResolver viewResolver( //
            @Autowired ServletContext servletContext, //
            @Value("${summer.web.freemarker.template-path:/WEB-INF/templates}") String templatePath, //
            @Value("${summer.web.freemarker.template-encoding:UTF-8}") String templateEncoding) {
        return new FreeMarkerViewResolver(servletContext, templatePath, templateEncoding);
    }

    @Bean
    ServletContext servletContext() {
        return (ServletContext) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { ServletContext.class }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
    }

    @Bean
    ServletContextPostProcessor servletContextPostProcessor() {
        return new ServletContextPostProcessor();
    }
}
