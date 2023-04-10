package com.itranswarp.summer.aop.before;

import com.itranswarp.summer.annotation.Bean;
import com.itranswarp.summer.annotation.ComponentScan;
import com.itranswarp.summer.annotation.Configuration;
import com.itranswarp.summer.aop.AroundProxyBeanPostProcessor;

@Configuration
@ComponentScan
public class BeforeApplication {

    @Bean
    AroundProxyBeanPostProcessor createAroundProxyBeanPostProcessor() {
        return new AroundProxyBeanPostProcessor();
    }
}
