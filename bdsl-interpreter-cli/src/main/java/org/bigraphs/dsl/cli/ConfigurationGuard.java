package org.bigraphs.dsl.cli;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationGuard implements InitializingBean, ApplicationListener<ContextRefreshedEvent>, ApplicationRunner {

    @Value("${disableBanner:#{null}}")
    private String disableBannerValue;

    public void afterPropertiesSet() {
        if (this.disableBannerValue == null || this.disableBannerValue.equals("${disableBanner}")) {
            throw new IllegalArgumentException("${my.home} must be configured");
        }
    }

    public static int counter;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        counter++;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("Application started with option names : " +
//                args.getOptionNames());
    }
}
