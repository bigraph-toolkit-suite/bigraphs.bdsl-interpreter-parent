package org.bigraphs.dsl.cli;

import org.bigraphs.dsl.cli.configuration.v1.BatchConfigurationV1;
import org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main entry point of the algebraic bigraph interpreter
 *
 * @author Dominik Grzelak
 */
@SpringBootApplication(
        scanBasePackageClasses = {ModelCheckingOptions.class, CliFactory.class},
        exclude = {DataSourceAutoConfiguration.class}
)
@Import(BatchConfigurationV1.class)
class Application implements CommandLineRunner {

    public static String[] addArgs(String[] args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
//        arguments.add("-Dspring.config.name=bdsl");
        return arguments.toArray(new String[0]);
    }

    @Value("${klo}")
    String logLevel;
    @Autowired
    Environment env;

    public static void main(String[] args) {
        String[] args2 = addArgs(args);
//        for (String arg : args2) {
//            System.out.println("arg: " + arg);
//        }
//        app.setBannerMode(Banner.Mode.OFF);
//        SpringApplication.run(Application.class, args2);
        SpringApplication app = new SpringApplication(Application.class);
        if (Arrays.asList(args2).contains("--disableBanner")) {
            app.setBannerMode(Banner.Mode.OFF);
        }
        app.run(args2);
    }

    @Autowired
    @Qualifier("cliFactory")
    CliFactory cliFactory;

//    @Autowired
//    ModelCheckingOptions modelCheckingOptions;


    @Override
    public void run(String... args) throws Exception {
//        CliOptionProvider cliOptionProvider = cliFactory.createCliOptionProvider();
        CliOptionProcessor cliProcessor = cliFactory.createCliOptionProcessor();
        CommandLineParser commandLineParser = cliFactory.getCommandLineParser();
        CliExecutor cliExecutor = cliFactory.createCliExecutor();
        try {
            cliProcessor.process(commandLineParser, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        CliOptionProcessor.ProcessorResult processorResult = cliProcessor.getProcessorResult();
        cliExecutor.execute(processorResult);
    }
}
