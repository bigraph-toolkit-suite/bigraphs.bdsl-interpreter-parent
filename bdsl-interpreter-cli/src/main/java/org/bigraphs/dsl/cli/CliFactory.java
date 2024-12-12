package org.bigraphs.dsl.cli;

import org.bigraphs.dsl.cli.configuration.BDSLExecutionProperties;
import org.bigraphs.dsl.interpreter.execution.jobs.reader.BaseBdslItemReader;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Dominik Grzelak
 */
public abstract class CliFactory {

    @Autowired
    protected JobLauncher jobLauncher;

    @Autowired
    @Qualifier("defaultStatementProcessingJob")
    protected Job defaultStatementProcessingJob;

    @Autowired
    protected BaseBdslItemReader reader;

    @Autowired
    protected BDSLExecutionProperties bdslExecutionProperties;

    @Autowired
    protected VersionHolder holder;

    protected abstract CliOptionProvider createCliOptionProvider();

    public abstract CliOptionProcessor createCliOptionProcessor();

    public abstract CliExecutor createCliExecutor();

    public CommandLineParser getCommandLineParser() {
        return new DefaultParser();
    }
}
