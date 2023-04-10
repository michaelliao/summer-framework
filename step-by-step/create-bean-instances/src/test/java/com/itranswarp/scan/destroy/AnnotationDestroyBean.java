package com.itranswarp.scan.destroy;

import com.itranswarp.summer.annotation.Component;
import com.itranswarp.summer.annotation.Value;

import jakarta.annotation.PreDestroy;

@Component
public class AnnotationDestroyBean {

    @Value("${app.title}")
    public String appTitle;

    @PreDestroy
    void destroy() {
        this.appTitle = null;
    }
}
