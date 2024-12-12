package org.bigraphs.dsl.cli.configuration.v1;

import org.bigraphs.dsl.bDSL.AbstractMainStatements;
import org.bigraphs.dsl.interpreter.execution.config.DefaultBdslBatchConfiguration;
import org.bigraphs.dsl.interpreter.execution.jobs.processor.BaseBdslStatementProcessor;
import org.bigraphs.dsl.interpreter.execution.jobs.reader.BaseBdslItemReader;
import org.bigraphs.dsl.interpreter.execution.jobs.writer.BaseBdslItemWriter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Dominik Grzelak
 */
@Configuration
public class BatchConfigurationV1 extends DefaultBdslBatchConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Override
    public BaseBdslItemReader reader() {
        return new SimpleSequentialBdslDocumentReader();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Override
    public BaseBdslStatementProcessor processor() {
        return new SimpleSequentialBdslStmtProcessor();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Override
    public BaseBdslItemWriter writer() {
        return new SimpleSequentialBdslStmtWriter();
    }

}
