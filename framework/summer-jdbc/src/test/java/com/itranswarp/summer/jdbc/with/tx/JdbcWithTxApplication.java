package com.itranswarp.summer.jdbc.with.tx;

import com.itranswarp.summer.annotation.ComponentScan;
import com.itranswarp.summer.annotation.Configuration;
import com.itranswarp.summer.annotation.Import;
import com.itranswarp.summer.jdbc.JdbcAutoConfiguration;

@ComponentScan
@Configuration
@Import(JdbcAutoConfiguration.class)
public class JdbcWithTxApplication {

}
