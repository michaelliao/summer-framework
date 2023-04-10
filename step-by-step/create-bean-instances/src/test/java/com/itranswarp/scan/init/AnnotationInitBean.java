package com.itranswarp.scan.init;

import com.itranswarp.summer.annotation.Component;
import com.itranswarp.summer.annotation.Value;

import jakarta.annotation.PostConstruct;

@Component
public class AnnotationInitBean {

    @Value("${app.title}")
    String appTitle;

    @Value("${app.version}")
    String appVersion;

    public String appName;

    @PostConstruct
    void init() {
        this.appName = this.appTitle + " / " + this.appVersion;
    }
}
