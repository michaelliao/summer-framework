package com.itranswarp.summer.web;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import com.itranswarp.summer.context.AnnotationConfigApplicationContext;
import com.itranswarp.summer.io.PropertyResolver;
import com.itranswarp.summer.web.controller.ControllerConfiguration;
import com.itranswarp.summer.web.controller.ApiController.SigninObj;
import com.itranswarp.summer.web.utils.JsonUtils;

import jakarta.servlet.ServletException;

public class DispatcherServletTest {

    DispatcherServlet dispatcherServlet;
    MockServletContext ctx;

    @Test
    void getHello() throws ServletException, IOException {
        var req = createMockRequest("GET", "/hello/Bob", null, null);
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertEquals("Hello, Bob", resp.getContentAsString());
    }

    @Test
    void getApiHello() throws ServletException, IOException {
        var req = createMockRequest("GET", "/api/hello/Bob", null, null);
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType());
        assertEquals("{\"name\":\"Bob\"}", resp.getContentAsString());
    }

    @Test
    void getGreeting() throws ServletException, IOException {
        var req = createMockRequest("GET", "/greeting", null, Map.of("name", "Bob"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertEquals("Hello, Bob", resp.getContentAsString());
    }

    @Test
    void getApiGreeting() throws ServletException, IOException {
        var req = createMockRequest("GET", "/api/greeting", null, Map.of("name", "Bob"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType());
        assertEquals("{\"action\":{\"name\":\"Bob\"}}", resp.getContentAsString());
    }

    @Test
    void getGreeting2() throws ServletException, IOException {
        var req = createMockRequest("GET", "/greeting", null, Map.of("action", "Morning", "name", "Bob"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertEquals("Morning, Bob", resp.getContentAsString());
    }

    @Test
    void getGreeting3() throws ServletException, IOException {
        var req = createMockRequest("GET", "/greeting", null, Map.of("action", "Morning"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(400, resp.getStatus());
    }

    @Test
    void getDownload() throws ServletException, IOException {
        var req = createMockRequest("GET", "/download/server.jar", null,
                Map.of("hasChecksum", "true", "length", "8", "time", "123.4", "md5", "aee9e38cb4d40ec2794542567539b4c8"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertArrayEquals("AAAAAAAA".getBytes(), resp.getContentAsByteArray());
    }

    @Test
    void getApiDownload() throws ServletException, IOException {
        var req = createMockRequest("GET", "/api/download/server.jar", null,
                Map.of("hasChecksum", "true", "length", "8", "time", "123.4", "md5", "aee9e38cb4d40ec2794542567539b4c8"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType());
        assertTrue(resp.getContentAsString().contains("\"file\":\"server.jar\""));
        assertTrue(resp.getContentAsString().contains("\"length\":8"));
        assertTrue(resp.getContentAsString().contains("\"content\":\"QUFBQUFBQUE=\""));
    }

    @Test
    void getDownloadPart() throws ServletException, IOException {
        var req = createMockRequest("GET", "/download-part", null, null);
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(206, resp.getStatus());
        assertEquals("bytes=100-108", resp.getHeader("Range"));
        assertArrayEquals("AAAAAAAA".getBytes(), resp.getContentAsByteArray());
    }

    @Test
    void getApiDownloadPart() throws ServletException, IOException {
        var req = createMockRequest("GET", "/api/download-part", null,
                Map.of("file", "server.jar", "hasChecksum", "true", "length", "8", "time", "123.4", "md5", "aee9e38cb4d40ec2794542567539b4c8"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType());
        assertTrue(resp.getContentAsString().contains("\"file\":\"server.jar\""));
        assertTrue(resp.getContentAsString().contains("\"length\":8"));
        assertTrue(resp.getContentAsString().contains("\"content\":\"QUFBQUFBQUE=\""));
    }

    @Test
    void getLogin() throws ServletException, IOException {
        var req = createMockRequest("GET", "/login", null, null);
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(302, resp.getStatus());
        assertEquals("/signin", resp.getRedirectedUrl());
    }

    @Test
    void getProduct() throws ServletException, IOException {
        var req = createMockRequest("GET", "/product/123", null, Map.of("name", "Bob"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("<h1>Hello, Bob</h1>"));
        assertTrue(resp.getContentAsString().contains("<a href=\"/product/123\">Summer Software</a>"));
    }

    @Test
    void postSignin() throws ServletException, IOException {
        var req = createMockRequest("POST", "/signin", null, Map.of("name", "Bob", "password", "hello123"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(302, resp.getStatus());
        assertEquals("/home?name=Bob", resp.getRedirectedUrl());
    }

    @Test
    void postRegister() throws ServletException, IOException {
        var req = createMockRequest("POST", "/register", null, Map.of("name", "Bob", "password", "hello123"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("<h1>Welcome, Bob</h1>"));
    }

    @Test
    void postApiRegister() throws ServletException, IOException {
        var signin = new SigninObj();
        signin.name = "Bob";
        signin.password = "hello123";
        var req = createMockRequest("POST", "/api/register", signin, null);
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType());
        assertEquals("[\"Bob\",true,12345]", resp.getContentAsString());
    }

    @Test
    void postSignout() throws ServletException, IOException {
        var req = createMockRequest("POST", "/signout", null, Map.of("name", "Bob"));
        var resp = createMockResponse();
        this.dispatcherServlet.service(req, resp);
        assertEquals(302, resp.getStatus());
        assertEquals("/signin?name=Bob", resp.getRedirectedUrl());
        assertEquals(Boolean.TRUE, req.getSession().getAttribute("signout"));
    }

    @BeforeEach
    void init() throws ServletException {
        this.ctx = createMockServletContext();
        WebMvcConfiguration.setServletContext(this.ctx);
        var propertyResolver = createPropertyResolver();
        var applicationContext = new AnnotationConfigApplicationContext(ControllerConfiguration.class, propertyResolver);
        this.dispatcherServlet = new DispatcherServlet(applicationContext, propertyResolver);
        this.dispatcherServlet.init();
    }

    PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        ps.put("app.title", "Scan App");
        ps.put("app.version", "v1.0");
        ps.put("summer.web.favicon-path", "/icon/favicon.ico");
        ps.put("summer.web.freemarker.template-path", "/WEB-INF/templates");
        ps.put("jdbc.username", "sa");
        ps.put("jdbc.password", "");
        var pr = new PropertyResolver(ps);
        return pr;
    }

    MockServletContext createMockServletContext() {
        Path path = Path.of("./src/test/resources").toAbsolutePath().normalize();
        var ctx = new MockServletContext("file://" + path.toString());
        ctx.setRequestCharacterEncoding("UTF-8");
        ctx.setResponseCharacterEncoding("UTF-8");
        return ctx;
    }

    MockHttpServletRequest createMockRequest(String method, String path, Object body, Map<String, String> params) {
        var req = new MockHttpServletRequest(this.ctx, method, path);
        if (method.equals("GET") && params != null) {
            params.keySet().forEach(key -> {
                req.setParameter(key, params.get(key));
            });
        } else if (method.equals("POST")) {
            if (body != null) {
                req.setContentType("application/json");
                req.setContent(JsonUtils.writeJson(body).getBytes(StandardCharsets.UTF_8));
            } else {
                req.setContentType("application/x-www-form-urlencoded");
                if (params != null) {
                    params.keySet().forEach(key -> {
                        req.setParameter(key, params.get(key));
                    });
                }
            }
        }
        var session = new MockHttpSession();
        req.setSession(session);
        return req;
    }

    MockHttpServletResponse createMockResponse() {
        var resp = new MockHttpServletResponse();
        resp.setDefaultCharacterEncoding("UTF-8");
        return resp;
    }
}
