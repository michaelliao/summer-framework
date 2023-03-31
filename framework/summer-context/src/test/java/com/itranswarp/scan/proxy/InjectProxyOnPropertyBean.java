package com.itranswarp.scan.proxy;

import com.itranswarp.summer.annotation.Autowired;
import com.itranswarp.summer.annotation.Component;

@Component
public class InjectProxyOnPropertyBean {

    @Autowired
    public OriginBean injected;
}
