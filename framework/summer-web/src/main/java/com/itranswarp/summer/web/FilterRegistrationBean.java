package com.itranswarp.summer.web;

import java.util.List;

import jakarta.servlet.Filter;

public abstract class FilterRegistrationBean {

    public abstract List<String> getUrlPatterns();

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
