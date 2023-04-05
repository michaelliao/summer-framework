package com.itranswarp.summer.web;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.Filter;

public abstract class FilterRegistrationBean {

    protected List<String> urlPatterns = new ArrayList<>(4);

    public String[] getUrlPatterns() {
        return this.urlPatterns.toArray(String[]::new);
    }

    public void setUrlPatterns(String... urlPatterns) {
        if (urlPatterns == null || urlPatterns.length == 0) {
            throw new IllegalArgumentException("Invalid url patterns.");
        }
        this.urlPatterns.clear();
        for (String urlPattern : urlPatterns) {
            this.urlPatterns.add(urlPattern);
        }
    }

    public abstract String getName();

    public abstract Filter getFilter();
}
