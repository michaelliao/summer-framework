package com.itranswarp.summer.web;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.summer.context.AnnotationConfigApplicationContext;
import com.itranswarp.summer.context.ApplicationContext;
import com.itranswarp.summer.io.PropertyResolver;
import com.itranswarp.summer.web.utils.WebUtils;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

public class ContextLoaderInitializer implements ServletContainerInitializer {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final Class<?> configClass;
    final PropertyResolver propertyResolver;

    public ContextLoaderInitializer(Class<?> configClass, PropertyResolver propertyResolver) {
        this.configClass = configClass;
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        logger.info("Servlet container start. ServletContext = {}", ctx);

        String encoding = propertyResolver.getProperty("${summer.web.character-encoding:UTF-8}");
        ctx.setRequestCharacterEncoding(encoding);
        ctx.setResponseCharacterEncoding(encoding);

        WebMvcConfiguration.setServletContext(ctx);
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(this.configClass, this.propertyResolver);
        logger.info("Application context created: {}", applicationContext);

        // register filters:
        WebUtils.registerFilters(ctx);
        // register DispatcherServlet:
        WebUtils.registerDispatcherServlet(ctx, this.propertyResolver);
    }
}
