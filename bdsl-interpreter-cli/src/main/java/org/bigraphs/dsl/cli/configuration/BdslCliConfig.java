package org.bigraphs.dsl.cli.configuration;

import org.bigraphs.dsl.cli.CliFactory;
import org.bigraphs.dsl.cli.VersionHolder;
import org.bigraphs.dsl.interpreter.execution.config.AbstractBdslBatchConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * Basic configuration for the command-line interpreter
 *
 * @author Dominik Grzelak
 */
@Configuration
public class BdslCliConfig {

    @Autowired
    private ApplicationContext context;

    @Bean
    public CliFactory cliFactory(@Value("${cli.factory.class:defaultCliFactory}") String qualifier) {
        return (CliFactory) context.getBean(qualifier);
    }

    @Bean
    VersionHolder versionHolder(ApplicationContext context) {
        return new VersionHolder(context);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocations(
//                new ClassPathResource("application.properties"),
                new ClassPathResource(AbstractBdslBatchConfigurer.DEFAULT_BDSL_EXECUTION_PROPERTIES_FILE)
        );
        propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
        propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(true);
        return propertySourcesPlaceholderConfigurer;
    }
}
