package com.itranswarp.summer.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PoliteInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object bean, Method method, Object[] args) throws Throwable {
        // 修改标记了@Polite的方法返回值:
        if (method.getAnnotation(Polite.class) != null) {
            String ret = (String) method.invoke(bean, args);
            if (ret.endsWith(".")) {
                ret = ret.substring(0, ret.length() - 1) + "!";
            }
            return ret;
        }
        return method.invoke(bean, args);
    }
}
