package com.itranswarp.scan;

import com.itranswarp.imported.LocalDateConfiguration;
import com.itranswarp.imported.ZonedDateConfiguration;
import com.itranswarp.summer.annotation.ComponentScan;
import com.itranswarp.summer.annotation.Import;
import com.itranswarp.summer.context.SummerApplication;

@ComponentScan
@Import({ LocalDateConfiguration.class, ZonedDateConfiguration.class })
public class ScanApplication {

    public static void main(String[] args) {
        SummerApplication.run(ScanApplication.class, args);
    }
}
