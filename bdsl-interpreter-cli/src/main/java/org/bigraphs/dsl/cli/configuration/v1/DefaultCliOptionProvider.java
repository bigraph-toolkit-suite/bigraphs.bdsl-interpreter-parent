package org.bigraphs.dsl.cli.configuration.v1;

import org.bigraphs.dsl.cli.CliOptionProvider;
import org.bigraphs.dsl.cli.configuration.BDSLExecutionProperties;
import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import java.util.*;

/**
 * An command-line option provider.
 * <p>
 * Generates and returns all available options for the {@link DefaultCliOptionProcessor}.
 *
 * @author Dominik Grzelak
 * @see DefaultCliOptionProcessor
 */
public class DefaultCliOptionProvider extends CliOptionProvider {
    public static final String OPTION_HELP = "help";
    public static final String OPTION_HELP_SHORT = "h";
    public static final String OPTION_VERSION = "version";
    public static final String OPTION_VERBOSE = "verbose";
    public static final String OPTION_VERBOSE_SHORT = "v";
    public static final String OPTION_DISABLEBANNER = "disableBanner";

    Options options = null;
    List<Option> optionList = null;
    List<OptionGroup> optionGroups = null;

    public DefaultCliOptionProvider(BDSLExecutionProperties bdslExecutionProperties) {
        super(bdslExecutionProperties);
    }

    public static class Execution {
        public static final String OPTION_ADDITIONAL_PROPERTIES = "D";
        public static final String OPTION_PROPERTIES_CYCLES = "model-checking.transitionOptions.maximumTransitions";
        public static final String OPTION_LABELS_TRANSITION_SYSTEM = "model-checking.exportOptions.printCanonicalStateLabel";
        public static final String OPTION_REACTION_GRAPH_FILE = "model-checking.exportOptions.reactionGraphFile";
        public static final String OPTION_OUTPUT_STATES_FOLDER = "model-checking.exportOptions.outputStatesFolder";
        public static final String OPTION_PROPERTIES_TIME_MAX = "model-checking.transitionOptions.maximumTime"; // in seconds
        public static final String OPTION_DEBUG_MEASURE_TIME = "model-checking.measure-time";
//        public static final String OPTION_INTERPRETATION_MODE = "imode";

        public static final Map<String, Class> availableOptions = new HashMap<String, Class>() {{
            put(OPTION_PROPERTIES_CYCLES, Integer.class);
            put(OPTION_PROPERTIES_TIME_MAX, Long.class);
            put(OPTION_REACTION_GRAPH_FILE, String.class);
            put(OPTION_OUTPUT_STATES_FOLDER, String.class);
            put(OPTION_LABELS_TRANSITION_SYSTEM, Boolean.class);
            put(OPTION_DEBUG_MEASURE_TIME, Boolean.class);
        }};
    }

    public static class BigraphModel {
        public static final String OPTION_ADDITIONAL_PROPERTIES = "B";
        public static final String OPTION_NAME = BdslMagicComments.ModelData.MODEL_NAME;
        public static final String OPTION_NS_URI = BdslMagicComments.ModelData.NS_URI;
        public static final String OPTION_NS_PREFIX = BdslMagicComments.ModelData.NS_PREFIX;
        public static final String OPTION_SCHEMA_LOCATION = BdslMagicComments.Export.SCHEMA_LOCATION;
    }

    public static class Environment {
        public static final String OPTION_OUTPUT_FOLDER = "outputDir";
        public static final String OPTION_INPUT_MODEL_FILE = "main";
        public static final String OPTION_INPUT_MODEL_FILE_SHORT = "m";
        public static final String OPTION_UDF_ARCHIVES = "includeUdf";
        public static final String OPTION_UDF_ARCHIVES_SHORT = "iudf";
        public static final String OPTION_INCLUDE_MODEL_FILE = "include";
        public static final String OPTION_INCLUDE_MODEL_FILE_SHORT = "i";
    }

    @Override
    public List<Option> getOptionElements() {
        if (Objects.isNull(optionList)) {
            optionList = new ArrayList<>();
            optionGroups = new ArrayList<>();

            Option help = Option.builder(OPTION_HELP_SHORT).longOpt(OPTION_HELP).hasArg(false).desc("print this message and exit").build();
            Option version = Option.builder(OPTION_VERSION).longOpt(OPTION_VERSION).hasArg(false).desc("print the version information and exit").build();
            Option verbose = Option.builder(OPTION_VERBOSE_SHORT).longOpt(OPTION_VERBOSE).hasArg(true).desc("Verbosity of the output. Possible values are: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF.").build();
            Option disableBanner = Option.builder(OPTION_DISABLEBANNER).longOpt(OPTION_DISABLEBANNER).hasArg(false).desc("Disables the banner at startup").build();

            Option modelfile = Option.builder(Environment.OPTION_INPUT_MODEL_FILE_SHORT)
                    .longOpt(Environment.OPTION_INPUT_MODEL_FILE)
                    .hasArg()
                    .type(String.class)
                    .argName("main program file")
                    .desc("the filename of the main BDSL file to interpret")
                    .build();

            Option includeFiles = Option.builder(Environment.OPTION_INCLUDE_MODEL_FILE_SHORT)
                    .longOpt(Environment.OPTION_INCLUDE_MODEL_FILE)
                    .hasArg()
                    .numberOfArgs(Option.UNLIMITED_VALUES)
                    .valueSeparator(',')
                    .type(String[].class)
                    .argName("filename(s) of include file(s)")
                    .optionalArg(true)
                    .desc("Additional file(s) to include. The file order is also the order in which the file(s) are loaded. Items are separated by a comma.")
                    .build();

            Option includeUdfArchives = Option.builder(Environment.OPTION_UDF_ARCHIVES_SHORT)
                    .longOpt(Environment.OPTION_UDF_ARCHIVES)
                    .hasArg()
                    .numberOfArgs(Option.UNLIMITED_VALUES)
                    .valueSeparator(',')
                    .type(String[].class)
                    .argName("filename(s) of udf archive(s)")
                    .optionalArg(true)
                    .desc("Additional user-defined function archive(s) to include. The file order is also the order in which the file(s) are loaded. Items are separated by a comma.")
                    .build();

            Option outputDirectory = Option.builder(Environment.OPTION_OUTPUT_FOLDER)
                    .longOpt(Environment.OPTION_OUTPUT_FOLDER)
                    .desc("The base output folder for all generated files.")
                    .hasArg()
                    .argName("name of output folder")
                    .build();

//            Option numOfCycles = Option.builder()
//                    .longOpt(Execution.OPTION_NUM_OF_CYCLES)
//                    .argName("cycles|time|...")
//                    .desc("number of cycles or time")
//                    .build();


            OptionGroup execOptionGroup = new OptionGroup();
//            Option interpretatioMode = Option.builder()
//                    .longOpt(Execution.OPTION_INTERPRETATION_MODE)
//                    .hasArg()
//                    .numberOfArgs(1)
//                    .optionalArg(true)
//                    .argName("mode of interpretation: [SIM | LEIM]")
//                    .desc("interpretation mode, either simple or listener execution")
//                    .build();
            Option executionAdditionalPropertiesOption = Option.builder(Execution.OPTION_ADDITIONAL_PROPERTIES)
//                    .longOpt(Execution.OPTION_EXECUTION_ADDITIONAL_PROPERTIES)
                    .argName("property=value")
                    .hasArgs()
                    .valueSeparator()
                    .numberOfArgs(2)
                    .desc("set the value for the given properties.")
                    .build();
            Option bigraphAdditionalOptions = Option.builder(BigraphModel.OPTION_ADDITIONAL_PROPERTIES)
//                    .longOpt(Execution.OPTION_EXECUTION_ADDITIONAL_PROPERTIES)
                    .argName("property=value")
                    .hasArgs()
                    .valueSeparator()
                    .numberOfArgs(2)
                    .desc("set the value for the given properties.")
                    .build();


//            Option filePathExecProps = Option.builder(Execution.OPTION_PROPERTY_FILE)
//                    .longOpt(Execution.OPTION_PROPERTY_FILE)
//                    .hasArg()
//                    .numberOfArgs(1)
//                    .optionalArg(true)
//                    .desc("Path to the exeuction specification property file")
//                    .argName("file path")
//                    .build();
            optionList.add(executionAdditionalPropertiesOption);
//            optionList.add(filePathExecProps);

            optionList.add(bigraphAdditionalOptions);


            optionList.add(help);
            optionList.add(version);
            optionList.add(verbose);
            optionList.add(disableBanner);
            optionList.add(modelfile);
            optionList.add(includeFiles);
            optionList.add(includeUdfArchives);
//            optionList.add(interpretatioMode);
            optionList.add(outputDirectory);

//            optionList.add(numOfCycles);
//            optionList.add(executionAdditionalPropertiesOption);
        }
        return optionList;
    }

    public Optional<Option> getOptionByName(String name) {
        return getOptionElements().stream().filter(x -> x.getOpt().equals(name) ||
                x.getLongOpt().equals(name)).findFirst();
    }

    @Override
    public Options getOptions() {
        if (Objects.isNull(options)) {
            options = new Options();
            getOptionElements().forEach(options::addOption);
            if (Objects.nonNull(optionGroups))
                optionGroups.forEach(options::addOptionGroup);
        }
        return options;
    }
}
