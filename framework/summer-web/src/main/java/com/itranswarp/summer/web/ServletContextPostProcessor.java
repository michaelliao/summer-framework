package com.itranswarp.summer.web;

import com.itranswarp.summer.context.BeanPostProcessor;

import jakarta.servlet.ServletContext;

/**
 * Replace mocked servlet context to real servlet context provided by servlet
 * container.
 */
public class ServletContextPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof ServletContext) {
            return servletContext;
        }
        return bean;
    }

    static ServletContext servletContext;

    public static void setServletContext(ServletContext ctx) {
        servletContext = ctx;
    }

}
