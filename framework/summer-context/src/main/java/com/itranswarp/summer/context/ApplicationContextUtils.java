package com.itranswarp.summer.context;

public class ApplicationContextUtils {

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }
}
