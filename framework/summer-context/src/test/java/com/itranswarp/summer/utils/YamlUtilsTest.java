package com.itranswarp.summer.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class YamlUtilsTest {

    @Test
    public void testLoadYaml() {
        Map<String, Object> configs = YamlUtils.loadYamlAsPlainMap("/application.yml");
        for (String key : configs.keySet()) {
            Object value = configs.get(key);
            System.out.println(key + ": " + value + " (" + value.getClass() + ")");
        }
        assertEquals("Summer Framework", configs.get("app.title"));
        assertEquals("1.0.0", configs.get("app.version"));
        assertNull(configs.get("app.author"));

        assertEquals("${AUTO_COMMIT:false}", configs.get("summer.datasource.auto-commit"));
        assertEquals("level-4", configs.get("other.deep.deep.level"));

        assertEquals("0x1a2b3c", configs.get("other.hex-data"));
        assertEquals("0x1a2b3c", configs.get("other.hex-string"));
    }
}
