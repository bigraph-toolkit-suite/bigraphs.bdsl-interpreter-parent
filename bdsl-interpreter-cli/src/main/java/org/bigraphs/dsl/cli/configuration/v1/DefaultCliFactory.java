package org.bigraphs.dsl.cli.configuration.v1;

import org.bigraphs.dsl.cli.CliExecutor;
import org.bigraphs.dsl.cli.CliFactory;
import org.bigraphs.dsl.cli.CliOptionProvider;
import org.bigraphs.dsl.cli.CliOptionProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Dominik Grzelak
 */
@Component("defaultCliFactory")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DefaultCliFactory extends CliFactory {

    @Override
    protected CliOptionProvider createCliOptionProvider() {
        return new DefaultCliOptionProvider(bdslExecutionProperties);
    }

    @Override
    public CliOptionProcessor createCliOptionProcessor() {
        return new DefaultCliOptionProcessor(createCliOptionProvider(), holder);
    }

    @Override
    public CliExecutor createCliExecutor() {
        return new DefaultCliExecutor(createCliOptionProvider())
                .withJob(defaultStatementProcessingJob)
                .withJobLauncher(jobLauncher)
                .withItemReader(reader);
    }
}
