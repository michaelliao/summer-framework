package com.itranswarp.hello.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.summer.annotation.Component;
import com.itranswarp.summer.annotation.Order;
import com.itranswarp.summer.web.FilterRegistrationBean;
import com.itranswarp.summer.web.utils.JsonUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(200)
@Component
public class ApiFilterRegistrationBean extends FilterRegistrationBean {

    @Override
    public List<String> getUrlPatterns() {
        return List.of("/api/*");
    }

    @Override
    public Filter getFilter() {
        return new ApiFilter();
    }
}

class ApiFilter implements Filter {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            logger.warn("api error when handle {}: {}", req.getMethod(), req.getRequestURI());
            if (!resp.isCommitted()) {
                resp.reset();
                resp.setStatus(400);
                PrintWriter pw = resp.getWriter();
                JsonUtils.writeJson(pw, Map.of("error", true, "type", e.getClass().getSimpleName(), "message", e.getMessage() == null ? "" : e.getMessage()));
                pw.flush();
            }
        }
    }
}