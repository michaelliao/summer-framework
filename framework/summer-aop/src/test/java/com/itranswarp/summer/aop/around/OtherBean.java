package com.itranswarp.summer.aop.around;

import com.itranswarp.summer.annotation.Autowired;
import com.itranswarp.summer.annotation.Component;
import com.itranswarp.summer.annotation.Order;

@Order(0)
@Component
public class OtherBean {

    public OriginBean origin;

    public OtherBean(@Autowired OriginBean origin) {
        this.origin = origin;
    }
}
