package com.itranswarp.summer.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.Filter;

public abstract class FilterRegistrationBean {

    protected List<String> urlPatterns = new ArrayList<>(4);

    public String[] getUrlPatterns() {
        return this.urlPatterns.toArray(String[]::new);
    }

    public void setUrlPatterns(Collection<String> urlPatterns) {
        Objects.requireNonNull(urlPatterns, "UrlPatterns must not be null");
        this.urlPatterns.clear();
        this.urlPatterns.addAll(urlPatterns);
    }

    public void addUrlPatterns(Collection<String> urlPatterns) {
        Objects.requireNonNull(urlPatterns, "UrlPatterns must not be null");
        this.urlPatterns.addAll(urlPatterns);
    }

    public abstract String getName();

    public abstract Filter getFilter();
}
