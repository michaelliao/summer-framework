package com.itranswarp.imported;

import java.time.ZonedDateTime;

import com.itranswarp.summer.annotation.Bean;
import com.itranswarp.summer.annotation.Configuration;

@Configuration
public class ZonedDateConfiguration {

    @Bean
    ZonedDateTime startZonedDateTime() {
        return ZonedDateTime.now();
    }
}
