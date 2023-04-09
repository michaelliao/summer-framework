package com.itranswarp.scan.destroy;

import com.itranswarp.summer.annotation.Bean;
import com.itranswarp.summer.annotation.Configuration;
import com.itranswarp.summer.annotation.Value;

@Configuration
public class SpecifyDestroyConfiguration {

    @Bean(destroyMethod = "destroy")
    SpecifyDestroyBean createSpecifyDestroyBean(@Value("${app.title}") String appTitle) {
        return new SpecifyDestroyBean(appTitle);
    }
}
