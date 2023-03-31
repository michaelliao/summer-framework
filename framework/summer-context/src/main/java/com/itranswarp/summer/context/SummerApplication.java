package com.itranswarp.summer.context;

import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.summer.io.PropertyResolver;
import com.itranswarp.summer.utils.ClassPathUtils;
import com.itranswarp.summer.utils.YamlUtils;

public class SummerApplication {

    static final String CONFIG_APP_YAML = "/application.yml";
    static final String CONFIG_APP_PROP = "/application.properties";

    final Logger logger = LoggerFactory.getLogger(getClass());
    final Class<?> configClass;
    final String[] args;

    SummerApplication(Class<?> configClass, String[] args) {
        this.configClass = configClass;
        this.args = args;
    }

    public static void run(Class<?> clazz, String... args) {
        new SummerApplication(clazz, args).run();
    }

    public void run() {
        // print banner:
        String banner = ClassPathUtils.readString("/banner.txt");
        banner.lines().forEach(System.out::println);

        // start info:
        final long startTime = System.currentTimeMillis();
        final int javaVersion = Runtime.version().feature();
        final long pid = ManagementFactory.getRuntimeMXBean().getPid();
        final String user = System.getProperty("user.name");
        final String pwd = Paths.get("").toAbsolutePath().toString();
        logger.info("Starting {} using Java {} with PID {} (started by {} in {})", this.configClass.getSimpleName(), javaVersion, pid, user, pwd);

        final Properties props = new Properties();
        // try load application.yml:
        try {
            Map<String, Object> ymlMap = YamlUtils.loadYamlAsPlainMap(CONFIG_APP_YAML);
            logger.info("load config: {}", CONFIG_APP_YAML);
            for (String key : ymlMap.keySet()) {
                Object value = ymlMap.get(key);
                if (value instanceof String strValue) {
                    props.put(key, strValue);
                }
            }
        } catch (UncheckedIOException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                // try load application.properties:
                ClassPathUtils.readInputStream(CONFIG_APP_PROP, (input) -> {
                    logger.info("load config: {}", CONFIG_APP_PROP);
                    props.load(input);
                    return true;
                });
            }
        }

        var propertyResolver = new PropertyResolver(props);

        ApplicationContext appContext = new AnnotationConfigApplicationContext(this.configClass, propertyResolver);

        // started info:
        final long endTime = System.currentTimeMillis();
        final String appTime = String.format("%.3f", (endTime - startTime) / 1000.0);
        final String jvmTime = String.format("%.3f", ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
        logger.info("Started {} in {} seconds (process running for {})", this.configClass.getSimpleName(), appTime, jvmTime);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        appContext.close();
    }
}
