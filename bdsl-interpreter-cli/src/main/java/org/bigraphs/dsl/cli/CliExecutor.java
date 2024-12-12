package org.bigraphs.dsl.cli;

import org.bigraphs.dsl.BDSLLib;
import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.cli.configuration.BDSLExecutionProperties;
import org.bigraphs.dsl.cli.configuration.v1.DefaultCliOptionProvider;
import org.bigraphs.dsl.interpreter.ParserService;
import org.bigraphs.dsl.interpreter.execution.jobs.reader.BaseBdslItemReader;
import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import static org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions.exportOpts;
import static org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions.transitionOpts;

/**
 * @author Dominik Grzelak
 */
public abstract class CliExecutor {
    protected ParserService parserService = new ParserService();//TODO get via service manager
    protected CliOptionProcessor.ProcessorResult processorResult;
    protected CliOptionProvider optionProvider;
    protected JobLauncher jobLauncher;
    protected Job job;
    protected BaseBdslItemReader itemReader;
    protected BDSLLib bdslLib = BDSLLib.getInstance();

    public CliExecutor(CliOptionProvider optionProvider) {
        this.optionProvider = optionProvider;
    }

    /**
     * Template method for {@link #execute(CliOptionProcessor.ProcessorResult)} to implement the concrete execution
     * behavior for interpreting and executing statements.
     * <p>
     * <b>Note:</b> may throw a runtime exception.
     *
     * @param bdslDocument the parsed BDSL program
     */
    public abstract void executeTemplate(BDSLDocument bdslDocument);

    public void execute(CliOptionProcessor.ProcessorResult processorResult) {
        this.processorResult = processorResult;

        loadUDFArchives();

        BDSLDocument brsModel = this.parseAndValidate();

        this.init(brsModel, processorResult);

        this.executeTemplate(brsModel);
    }

    protected void loadUDFArchives() {
        // search for UDF archives in the default location first, i.e., the "main directory" (where the BDSL program is executed)
        if (processorResult.getModelFile() != null) {
            File parentDir = processorResult.getModelFile().getParentFile();
            String parentConfigDir = Paths.get(parentDir.getAbsolutePath(), "config").toAbsolutePath().toString();
            List<JarFile> allJars = bdslLib.findAllUdfJars(Arrays.asList(parentDir.toString(), parentConfigDir));
            if (allJars != null) {
                bdslLib.addToClasspath(allJars.toArray(new JarFile[0]));
            }
        }
        // process explicitly defined UDF archives
        if (processorResult.getIncludeUdfArchives() != null) {
            bdslLib.addToClasspath(processorResult.getIncludeUdfArchives());
        }
    }

    protected BDSLDocument parseAndValidate() {
        String[] includes = new String[]{};
        if (processorResult.getIncludeFiles() != null) {
            includes = Arrays.stream(processorResult.getIncludeFiles()).map(File::getAbsolutePath).toArray(String[]::new);
        }
        BDSLDocument document = parserService.parse(processorResult.getModelFile().getAbsolutePath(), includes, true);
        this.parserService.validate(document);
        return document;
    }

    public void init(BDSLDocument brsModel, CliOptionProcessor.ProcessorResult processorResult) {
        // Update magic comments coming from the passed arguments
        // (have higher precedence than the ones specified in the *.bdsl file)
        List<BdslMagicComments.Comment> commentsParsed = BdslMagicComments.parse(brsModel);
        List<BdslMagicComments.Comment> commentsFromArguments = processorResult.getMagicComments();
        for (BdslMagicComments.Comment each : commentsParsed) {
            if (!BdslMagicComments.getValue(commentsFromArguments, each.KEY).isPresent()) {
                Optional<BdslMagicComments.Comment> value = BdslMagicComments.getValue(commentsParsed, each.KEY);
                value.ifPresent(v -> BdslMagicComments.setValue(commentsFromArguments, v.KEY, v.VALUE));
            }
        }

        // Update the model checking options coming from the passed arguments
        // (have higher precedence than the ones specified in the *.properties file)
        BDSLExecutionProperties bdslConfigProperties = processorResult.getBDSLExecutionProperties();
        ModelCheckingOptions modelCheckingOptions = bdslConfigProperties.getModelCheckingOptions();

        // BRS transition-related options
        ModelCheckingOptions.TransitionOptions transOpts = modelCheckingOptions.get(ModelCheckingOptions.Options.TRANSITION);
        if (transOpts == null) modelCheckingOptions.and(transOpts = transitionOpts().create());
        ModelCheckingOptions.TransitionOptions.Builder transOptsBuilder = transOpts.toBuilder();
        bdslConfigProperties.get(DefaultCliOptionProvider.Execution.OPTION_PROPERTIES_CYCLES, Integer.class)
                .ifPresent(x -> transOptsBuilder.setMaximumTransitions(x));
        bdslConfigProperties.get(DefaultCliOptionProvider.Execution.OPTION_PROPERTIES_TIME_MAX, Long.class)
                .ifPresent(x -> transOptsBuilder.setMaximumTime(x, TimeUnit.MILLISECONDS));

        // Export-related options
        ModelCheckingOptions.ExportOptions expOpts = modelCheckingOptions.get(ModelCheckingOptions.Options.EXPORT);
        if (expOpts == null) modelCheckingOptions.and(expOpts = exportOpts().create());
        ModelCheckingOptions.ExportOptions.Builder expOptsbuilder = expOpts.toBuilder();
        bdslConfigProperties.get(DefaultCliOptionProvider.Execution.OPTION_LABELS_TRANSITION_SYSTEM, Boolean.class)
                .ifPresent(x -> expOptsbuilder.setPrintCanonicalStateLabel(x));
        bdslConfigProperties.get(DefaultCliOptionProvider.Execution.OPTION_REACTION_GRAPH_FILE, String.class)
                .ifPresent(x -> expOptsbuilder.setReactionGraphFile(new File(x)));
        bdslConfigProperties.get(DefaultCliOptionProvider.Execution.OPTION_OUTPUT_STATES_FOLDER, String.class)
                .ifPresent(x -> expOptsbuilder.setOutputStatesFolder(new File(x)));

        bdslConfigProperties.get(DefaultCliOptionProvider.Execution.OPTION_DEBUG_MEASURE_TIME, Boolean.class)
                .ifPresent(x -> modelCheckingOptions.setMeasureTime(x));

        modelCheckingOptions.and(transOptsBuilder.create()).and(expOptsbuilder.create());
    }

    public CliExecutor withJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
        return this;
    }

    public CliExecutor withJob(Job job) {
        this.job = job;
        return this;
    }

    public CliExecutor withItemReader(BaseBdslItemReader itemReader) {
        this.itemReader = itemReader;
        return this;
    }
}
