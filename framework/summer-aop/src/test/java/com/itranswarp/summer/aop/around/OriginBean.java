package com.itranswarp.summer.aop.around;

import com.itranswarp.summer.annotation.Around;
import com.itranswarp.summer.annotation.Component;
import com.itranswarp.summer.annotation.Value;

@Component
@Around("aroundInvocationHandler")
public class OriginBean {

    @Value("${customer.name}")
    public String name;

    @Polite
    public String hello() {
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
