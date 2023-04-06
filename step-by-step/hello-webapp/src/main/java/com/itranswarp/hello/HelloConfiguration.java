package com.itranswarp.hello;

import com.itranswarp.summer.annotation.ComponentScan;
import com.itranswarp.summer.annotation.Configuration;
import com.itranswarp.summer.annotation.Import;
import com.itranswarp.summer.jdbc.JdbcConfiguration;
import com.itranswarp.summer.web.WebMvcConfiguration;

@ComponentScan
@Configuration
@Import({ JdbcConfiguration.class, WebMvcConfiguration.class })
public class HelloConfiguration {

}
