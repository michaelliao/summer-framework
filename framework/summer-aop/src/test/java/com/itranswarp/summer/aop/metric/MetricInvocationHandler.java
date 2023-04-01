package com.itranswarp.summer.aop.metric;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.summer.annotation.Component;

@Component
public class MetricInvocationHandler implements InvocationHandler {

    final Logger logger = LoggerFactory.getLogger(getClass());

    public Map<String, Long> lastProcessedTime = new HashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Metric metric = method.getAnnotation(Metric.class);
        if (metric == null) {
            // do not do performance test:
            return method.invoke(proxy, args);
        }
        String name = metric.value();
        long start = System.currentTimeMillis();
        try {
            return method.invoke(proxy, args);
        } finally {
            long end = System.currentTimeMillis();
            long execTime = end - start;
            if (name.equals("MD5")) {
                execTime = 5;
            }
            if (name.equals("SHA-256")) {
                execTime = 256;
            }
            logger.info("log metric time: {} = {}", name, execTime);
            lastProcessedTime.put(name, execTime);
        }
    }
}
