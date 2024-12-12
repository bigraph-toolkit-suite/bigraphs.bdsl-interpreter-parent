package org.bigraphs.dsl.interpreter.execution.config;

import org.bigraphs.dsl.interpreter.execution.jobs.processor.BaseBdslStatementProcessor;
import org.bigraphs.dsl.interpreter.execution.jobs.reader.BaseBdslItemReader;
import org.bigraphs.dsl.interpreter.execution.jobs.writer.BaseBdslItemWriter;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Dominik Grzelak
 */
@Configuration
@EnableBatchProcessing
public abstract class AbstractBdslBatchConfigurer {

    public static final String DEFAULT_BDSL_EXECUTION_PROPERTIES_FILE = "bdsl-execution.properties";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public abstract BaseBdslItemReader reader();

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public abstract BaseBdslStatementProcessor processor();

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public abstract BaseBdslItemWriter writer();

}
