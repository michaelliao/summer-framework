package com.itranswarp.imported;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itranswarp.summer.annotation.Bean;
import com.itranswarp.summer.annotation.Configuration;

@Configuration
public class LocalDateConfiguration {

    @Bean
    LocalDate startLocalDate() {
        return LocalDate.now();
    }

    @Bean
    LocalDateTime startLocalDateTime() {
        return LocalDateTime.now();
    }
}
