package com.itranswarp.summer.aop.after;

import com.itranswarp.summer.annotation.Around;
import com.itranswarp.summer.annotation.Component;

@Component
@Around("politeInvocationHandler")
public class GreetingBean {

    public String hello(String name) {
        return "Hello, " + name + ".";
    }

    public String morning(String name) {
        return "Morning, " + name + ".";
    }
}
