package com.itranswarp.summer.web.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletException;

public class PathUtilsTest {

    @Test
    void validPath() throws Exception {
        Pattern p = PathUtils.compile("/test/{userId}/{postId}");
        Matcher m1 = p.matcher("/test/12345/a123");
        assertTrue(m1.matches());
        assertEquals("12345", m1.group("userId"));
        assertEquals("a123", m1.group("postId"));

        Matcher m2 = p.matcher("/test/12345/a/123");
        assertFalse(m2.matches());
    }

    @Test
    void validPath2() throws Exception {
        Pattern p = PathUtils.compile("/test/{a123}");
        Matcher m1 = p.matcher("/test/12345");
        assertTrue(m1.matches());
        assertEquals("12345", m1.group("a123"));
    }

    @Test
    void invalidPath() throws Exception {
        assertThrows(ServletException.class, () -> {
            PathUtils.compile("/no-name/{}");
        });
        assertThrows(ServletException.class, () -> {
            PathUtils.compile("/starts-with-digit/{123}");
        });
        assertThrows(ServletException.class, () -> {
            PathUtils.compile("/invalid-name/{user-id}");
        });
        assertThrows(ServletException.class, () -> {
            PathUtils.compile("/invalid-name/{user_id}");
        });
        assertThrows(ServletException.class, () -> {
            PathUtils.compile("/missing-right/{user/");
        });
        assertThrows(ServletException.class, () -> {
            PathUtils.compile("/missing-left/user}/");
        });
    }
}
