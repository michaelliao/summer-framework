package com.itranswarp.summer.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    private static ProxyResolver INSTANCE = null;

    public static ProxyResolver getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProxyResolver();
        }
        return INSTANCE;
    }

    private ProxyResolver() {
    }

    @SuppressWarnings("unchecked")
    public <T, A extends Annotation> T createProxy(T bean, InvocationHandler handler) {
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

    @SuppressWarnings("unchecked")
    <T> Class<T> getParameterizedType(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " does not have parameterized type.");
        }
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        if (types.length != 1) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " has more than 1 parameterized types.");
        }
        Type r = types[0];
        if (!(r instanceof Class<?>)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " does not have parameterized type of class.");
        }
        return (Class<T>) r;
    }
}
