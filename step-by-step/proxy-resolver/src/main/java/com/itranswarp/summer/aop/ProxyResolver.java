package com.itranswarp.summer.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Create proxy by subclassing and override methods with interceptor.
 */
public class ProxyResolver {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final ByteBuddy byteBuddy = new ByteBuddy();

    @SuppressWarnings("unchecked")
    public <T> T createProxy(T bean, InvocationHandler handler) {
        Class<?> targetClass = bean.getClass();
        logger.atDebug().log("create proxy for bean {} @{}", targetClass.getName(), Integer.toHexString(bean.hashCode()));
        Class<?> proxyClass = this.byteBuddy
                // subclass with default empty constructor:
                .subclass(targetClass, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
                // intercept methods:
                .method(ElementMatchers.isPublic()).intercept(InvocationHandlerAdapter.of(
                        // proxy method invoke:
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                // delegate to origin bean:
                                return handler.invoke(bean, method, args);
                            }
                        }))
                // generate proxy class:
                .make().load(targetClass.getClassLoader()).getLoaded();
        Object proxy;
        try {
            proxy = proxyClass.getConstructor().newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (T) proxy;
    }
}
