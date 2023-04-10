package com.itranswarp.scan.nested;

import com.itranswarp.summer.annotation.Component;

@Component
public class OuterBean {

    @Component
    public static class NestedBean {

    }
}
