package org.bigraphs.dsl.cli;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

/**
 * @see <a href="https://blog.jdriven.com/2018/10/get-your-application-version-with-spring-boot/">Source: https://blog.jdriven.com/2018/10/get-your-application-version-with-spring-boot/</a>
 */
public class VersionHolder {

    private final String version;

    public VersionHolder(ApplicationContext context) {
        version = context.getBeansWithAnnotation(SpringBootApplication.class).entrySet().stream()
                .findFirst()
                .flatMap(es -> {
                    final String implementationVersion = es.getValue().getClass().getPackage().getImplementationVersion();
                    return Optional.ofNullable(implementationVersion);
                }).orElse("unknown");
    }

    public String getVersion() {
        return version;
    }
}
