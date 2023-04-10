package com.itranswarp.scan;

import com.itranswarp.imported.LocalDateConfiguration;
import com.itranswarp.imported.ZonedDateConfiguration;
import com.itranswarp.summer.annotation.ComponentScan;
import com.itranswarp.summer.annotation.Import;

@ComponentScan
@Import({ LocalDateConfiguration.class, ZonedDateConfiguration.class })
public class ScanApplication {

}
