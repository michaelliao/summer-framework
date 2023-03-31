package com.itranswarp.scan.primary;

import com.itranswarp.summer.annotation.Bean;
import com.itranswarp.summer.annotation.Configuration;
import com.itranswarp.summer.annotation.Primary;

@Configuration
public class PrimaryConfiguration {

    @Primary
    @Bean
    DogBean husky() {
        return new DogBean("Husky");
    }

    @Bean
    DogBean teddy() {
        return new DogBean("Teddy");
    }
}
