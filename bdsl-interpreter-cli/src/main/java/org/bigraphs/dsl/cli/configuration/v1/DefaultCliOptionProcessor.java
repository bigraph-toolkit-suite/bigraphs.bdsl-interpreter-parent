package org.bigraphs.dsl.cli.configuration.v1;

import org.bigraphs.dsl.cli.CliOptionProcessor;
import org.bigraphs.dsl.cli.CliOptionProvider;
import org.bigraphs.dsl.cli.FileLoadUtil;
import org.bigraphs.dsl.cli.VersionHolder;
import org.bigraphs.dsl.cli.configuration.BDSLExecutionProperties;
import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.io.File;
import java.util.*;

/**
 * @author Dominik Grzelak
 */
public class DefaultCliOptionProcessor extends CliOptionProcessor {
    private final ProcessorResult.Builder builder = ProcessorResult.builder();
    private ProcessorResult result;

    public DefaultCliOptionProcessor(CliOptionProvider optionProvider, VersionHolder holder) {
        super(optionProvider, holder);
    }

    @Override
    public ProcessorResult process(CommandLineParser parser, String[] args) throws ParseException {
        CommandLine cmdLine = parser.parse(getOptionProvider().getOptions(), args);
        if (args.length == 0) {
            System.out.println("No arguments specified. Interpreter exits now.");
            System.exit(0);
        }
        if (cmdLine.hasOption(DefaultCliOptionProvider.OPTION_HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(110);
            formatter.setLeftPadding(1);
            formatter.setLongOptSeparator("=");
            formatter.printHelp("BDSL interpreter tool", getOptionProvider().getOptions());
            System.exit(0);
            return builder.build();
        }

        if (cmdLine.hasOption(DefaultCliOptionProvider.OPTION_VERSION)) {
            System.out.printf("Version: %s%n", versionHolder.getVersion());
            System.exit(0);
            return builder.build();
        }


        MutableList<BdslMagicComments.Comment> bdslMagicComments = Lists.mutable.empty();
        // read BDSL properties
        BDSLExecutionProperties bdslExecutionProperties = optionProvider.getBdslExecutionProperties();
        builder.addBdslExecutionProperties(bdslExecutionProperties);

        if (cmdLine.hasOption(DefaultCliOptionProvider.Execution.OPTION_ADDITIONAL_PROPERTIES)) {
            Properties optionProperties = cmdLine.getOptionProperties(DefaultCliOptionProvider.Execution.OPTION_ADDITIONAL_PROPERTIES);

            DefaultCliOptionProvider.Execution.availableOptions.forEach((key, clazzValue) -> {
                String property3 = optionProperties.getProperty(key);
                if (Objects.nonNull(property3)) {
                    bdslExecutionProperties.set(key, convertString(property3, clazzValue));
                }
            });
        }

        if (cmdLine.hasOption(DefaultCliOptionProvider.BigraphModel.OPTION_ADDITIONAL_PROPERTIES)) {
            Properties opts = cmdLine.getOptionProperties(DefaultCliOptionProvider.BigraphModel.OPTION_ADDITIONAL_PROPERTIES);
            String name = opts.getProperty(DefaultCliOptionProvider.BigraphModel.OPTION_NAME);
            String nsPrefix = opts.getProperty(DefaultCliOptionProvider.BigraphModel.OPTION_NS_PREFIX);
            String nsUri = opts.getProperty(DefaultCliOptionProvider.BigraphModel.OPTION_NS_URI);
            String schemaLocation = opts.getProperty(DefaultCliOptionProvider.BigraphModel.OPTION_SCHEMA_LOCATION);

            if (Objects.nonNull(name)) {
                bdslMagicComments.add(
                        new BdslMagicComments.Comment(BdslMagicComments.ModelData.MODEL_NAME, name));
            }
            if (Objects.nonNull(nsPrefix)) {
                bdslMagicComments.add(
                        new BdslMagicComments.Comment(BdslMagicComments.ModelData.NS_PREFIX, nsPrefix));
            }
            if (Objects.nonNull(nsUri)) {
                bdslMagicComments.add(
                        new BdslMagicComments.Comment(BdslMagicComments.ModelData.NS_URI, nsUri));
            }
            if (Objects.nonNull(schemaLocation)) {
                bdslMagicComments.add(
                        new BdslMagicComments.Comment(BdslMagicComments.Export.SCHEMA_LOCATION, schemaLocation));
            }

            builder.addMagicComments(bdslMagicComments);
        }

        if (cmdLine.hasOption(DefaultCliOptionProvider.Environment.OPTION_INPUT_MODEL_FILE)) {
            String filename = cmdLine.getOptionValue(DefaultCliOptionProvider.Environment.OPTION_INPUT_MODEL_FILE);
            builder.addModelFile(loadFile(filename));
        } else {
            Optional<String> main = bdslExecutionProperties.get(DefaultCliOptionProvider.Environment.OPTION_INPUT_MODEL_FILE, String.class);
            if (main.isPresent()) {
                builder.addModelFile(loadFile(main.get()));
            }
        }

        if (cmdLine.hasOption(DefaultCliOptionProvider.Environment.OPTION_INCLUDE_MODEL_FILE)) {
            String[] includes = cmdLine.getOptionValues(DefaultCliOptionProvider.Environment.OPTION_INCLUDE_MODEL_FILE);
            builder.addIncludeFiles(loadFiles(includes));
        } else {
            Optional<String[]> includes = bdslExecutionProperties.get(DefaultCliOptionProvider.Environment.OPTION_INCLUDE_MODEL_FILE, String[].class);
            if (includes.isPresent())
                builder.addIncludeFiles(loadFiles(includes.get()));
        }

        if (cmdLine.hasOption(DefaultCliOptionProvider.Environment.OPTION_UDF_ARCHIVES)) {
            String[] includes = cmdLine.getOptionValues(DefaultCliOptionProvider.Environment.OPTION_UDF_ARCHIVES);
            builder.addUdfFiles(loadFiles(includes));
        } else {
            Optional<String[]> includes = bdslExecutionProperties.get(DefaultCliOptionProvider.Environment.OPTION_UDF_ARCHIVES, String[].class);
            if (includes.isPresent())
                builder.addUdfFiles(loadFiles(includes.get()));
        }

        if (cmdLine.hasOption(DefaultCliOptionProvider.Environment.OPTION_OUTPUT_FOLDER)) {
            String folder = cmdLine.getOptionValue(DefaultCliOptionProvider.Environment.OPTION_OUTPUT_FOLDER);
            builder.addBaseOutputFolder(new File(folder));
        } else {
            Optional<String> folder = bdslExecutionProperties.get(DefaultCliOptionProvider.Environment.OPTION_OUTPUT_FOLDER, String.class);
            if (folder.isPresent()) {
                builder.addBaseOutputFolder(new File(folder.get()));
            }
        }

        return builder.build();
    }

    private File loadFile(String filename) throws ParseException {
        File[] files = loadFiles(new String[]{filename});
        if (files.length >= 1)
            return files[0];
        throw new ParseException("Could not load file");
    }

    private File[] loadFiles(String[] includeFilenames) throws ParseException {
        List<File> includeFiles = new ArrayList<>();
        for (String f : includeFilenames) {
            if (Objects.isNull(f) || f.trim().isEmpty()) continue;
            Optional<File> load = FileLoadUtil.load(f);
            if (!load.isPresent()) throw new ParseException("Could not load model file at " + f);
            File includeFile = load.get();
            includeFiles.add(includeFile);
        }
        return includeFiles.toArray(new File[0]);
    }

    private Object convertString(String value, Class<?> clazz) {
        if (Number.class.isAssignableFrom(clazz)) {
            if (Integer.class.equals(clazz)) {
                return NumberUtils.toInt(value, 0);
            }
            if (Long.class.equals(clazz)) {
                return NumberUtils.toLong(value, 0);
            }
        }
        if (Boolean.class.isAssignableFrom(clazz)) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }

    @Override
    public ProcessorResult getProcessorResult() {
        return builder.build();
    }
}
