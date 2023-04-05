package com.itranswarp.summer.web;

import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface View {

    @Nullable
    default String getContentType() {
        return null;
    }

    void render(@Nullable Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
