package com.itranswarp.scan.init;

import com.itranswarp.summer.annotation.Bean;
import com.itranswarp.summer.annotation.Configuration;
import com.itranswarp.summer.annotation.Value;

@Configuration
public class SpecifyInitConfiguration {

    @Bean(initMethod = "init")
    SpecifyInitBean createSpecifyInitBean(@Value("${app.title}") String appTitle, @Value("${app.version}") String appVersion) {
        return new SpecifyInitBean(appTitle, appVersion);
    }
}
