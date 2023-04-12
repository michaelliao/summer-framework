package com.itranswarp.summer.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.summer.context.AnnotationConfigApplicationContext;
import com.itranswarp.summer.context.ApplicationContext;
import com.itranswarp.summer.exception.NestedRuntimeException;
import com.itranswarp.summer.io.PropertyResolver;
import com.itranswarp.summer.web.utils.WebUtils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ContextLoaderListener implements ServletContextListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("init {}.", getClass().getName());
        var servletContext = sce.getServletContext();
        WebMvcConfiguration.setServletContext(servletContext);

        var propertyResolver = WebUtils.createPropertyResolver();
        String encoding = propertyResolver.getProperty("${summer.web.character-encoding:UTF-8}");
        servletContext.setRequestCharacterEncoding(encoding);
        servletContext.setResponseCharacterEncoding(encoding);
        var applicationContext = createApplicationContext(servletContext.getInitParameter("configuration"), propertyResolver);
        // register filters:
        WebUtils.registerFilters(servletContext);
        // register DispatcherServlet:
        WebUtils.registerDispatcherServlet(servletContext, propertyResolver);

        servletContext.setAttribute("applicationContext", applicationContext);
    }

    ApplicationContext createApplicationContext(String configClassName, PropertyResolver propertyResolver) {
        logger.info("init ApplicationContext by configuration: {}", configClassName);
        if (configClassName == null || configClassName.isEmpty()) {
            throw new NestedRuntimeException("Cannot init ApplicationContext for missing init param name: configuration");
        }
        Class<?> configClass;
        try {
            configClass = Class.forName(configClassName);
        } catch (ClassNotFoundException e) {
            throw new NestedRuntimeException("Could not load class from init param 'configuration': " + configClassName);
        }
        return new AnnotationConfigApplicationContext(configClass, propertyResolver);
    }
}
