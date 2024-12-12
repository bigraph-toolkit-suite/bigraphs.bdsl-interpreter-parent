package org.bigraphs.dsl.cli.configuration.v1;

import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.cli.CliExecutor;
import org.bigraphs.dsl.cli.CliOptionProvider;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

import java.util.logging.Logger;

/**
 * @author Dominik Grzelak
 */
public class DefaultCliExecutor extends CliExecutor {
//    private static final Logger LOG = LogManager.getLogger(DefaultCliExecutor.class);

    public DefaultCliExecutor(CliOptionProvider optionProvider) {
        super(optionProvider);
    }

    @Override
    public void executeTemplate(BDSLDocument bdslDocument) {
//        LOG.debug("Starting the batch interpretation job");
        itemReader.setBdslDocument(bdslDocument);
        itemReader.prepare();
        try {
            JobExecution execution = jobLauncher.run(job, new JobParameters());
//            LOG.debug("Job Status : " + execution.getStatus());
//            LOG.debug("Job completed");
        } catch (Exception e) {
//            LOG.error(e);
            throw new RuntimeException(e);
        }
    }
}
