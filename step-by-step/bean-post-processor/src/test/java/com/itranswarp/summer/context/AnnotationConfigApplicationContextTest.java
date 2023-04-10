package com.itranswarp.summer.context;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.itranswarp.scan.ScanApplication;
import com.itranswarp.scan.proxy.InjectProxyOnConstructorBean;
import com.itranswarp.scan.proxy.InjectProxyOnPropertyBean;
import com.itranswarp.scan.proxy.OriginBean;
import com.itranswarp.scan.proxy.SecondProxyBean;
import com.itranswarp.summer.io.PropertyResolver;

public class AnnotationConfigApplicationContextTest {

    @Test
    public void testProxy() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        // test proxy:
        OriginBean proxy = ctx.getBean(OriginBean.class);
        assertSame(SecondProxyBean.class, proxy.getClass());
        assertEquals("Scan App", proxy.getName());
        assertEquals("v1.0", proxy.getVersion());
        // make sure proxy.field is not injected:
        assertNull(proxy.name);
        assertNull(proxy.version);

        // other beans are injected proxy instance:
        var inject1 = ctx.getBean(InjectProxyOnPropertyBean.class);
        var inject2 = ctx.getBean(InjectProxyOnConstructorBean.class);
        assertSame(proxy, inject1.injected);
        assertSame(proxy, inject2.injected);
    }

    PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        ps.put("app.title", "Scan App");
        ps.put("app.version", "v1.0");
        ps.put("jdbc.url", "jdbc:hsqldb:file:testdb.tmp");
        ps.put("jdbc.username", "sa");
        ps.put("jdbc.password", "");
        ps.put("convert.boolean", "true");
        ps.put("convert.byte", "123");
        ps.put("convert.short", "12345");
        ps.put("convert.integer", "1234567");
        ps.put("convert.long", "123456789000");
        ps.put("convert.float", "12345.6789");
        ps.put("convert.double", "123456789.87654321");
        ps.put("convert.localdate", "2023-03-29");
        ps.put("convert.localtime", "20:45:01");
        ps.put("convert.localdatetime", "2023-03-29T20:45:01");
        ps.put("convert.zoneddatetime", "2023-03-29T20:45:01+08:00[Asia/Shanghai]");
        ps.put("convert.duration", "P2DT3H4M");
        ps.put("convert.zoneid", "Asia/Shanghai");
        var pr = new PropertyResolver(ps);
        return pr;
    }
}
