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

    /**
     * Get name by class name. Example:
     * 
     * ApiFilterRegistrationBean -> apiFilter
     * 
     * ApiFilterRegistration -> apiFilter
     * 
     * ApiFilterReg -> apiFilterReg
     */
    public String getName() {
        String name = getClass().getSimpleName();
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        if (name.endsWith("FilterRegistrationBean") && name.length() > "FilterRegistrationBean".length()) {
            return name.substring(0, name.length() - "FilterRegistrationBean".length());
        }
        if (name.endsWith("FilterRegistration") && name.length() > "FilterRegistration".length()) {
            return name.substring(0, name.length() - "FilterRegistration".length());
        }
        return name;
    };

    public abstract Filter getFilter();
}
