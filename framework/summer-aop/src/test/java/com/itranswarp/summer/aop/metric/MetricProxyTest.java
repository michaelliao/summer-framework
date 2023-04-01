package com.itranswarp.summer.aop.metric;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.itranswarp.summer.context.AnnotationConfigApplicationContext;
import com.itranswarp.summer.io.PropertyResolver;

public class MetricProxyTest {

    @Test
    public void testMetricProxy() {
        try (var ctx = new AnnotationConfigApplicationContext(MetricApplication.class, createPropertyResolver())) {
            HashWorker worker = ctx.getBean(HashWorker.class);

            // proxy class, not origin class:
            assertNotSame(HashWorker.class, worker.getClass());

            String md5 = "0x5d41402abc4b2a76b9719d911017c592";
            String sha1 = "0xaaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d";
            String sha256 = "0x2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";

            assertEquals(md5, worker.md5("hello"));
            assertEquals(sha1, worker.sha1("hello"));
            assertEquals(sha256, worker.sha256("hello"));

            // get metric time:
            MetricInvocationHandler metrics = ctx.getBean(MetricInvocationHandler.class);
            assertEquals(5, metrics.lastProcessedTime.get("MD5"));
            assertEquals(256, metrics.lastProcessedTime.get("SHA-256"));
            // cannot metric sha1() because it is a final method:
            assertNull(metrics.lastProcessedTime.get("SHA-1"));
        }
    }

    PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        var pr = new PropertyResolver(ps);
        return pr;
    }
}
