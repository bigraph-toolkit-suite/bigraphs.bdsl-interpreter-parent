package org.bigraphs.dsl.cli;

import org.bigraphs.dsl.cli.configuration.BDSLExecutionProperties;
import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominik Grzelak
 */
public abstract class CliOptionProcessor {

    protected CliOptionProvider optionProvider;
    protected VersionHolder versionHolder;

    protected CliOptionProcessor(CliOptionProvider optionProvider, VersionHolder versionHolder) {
        this.optionProvider = optionProvider;
        this.versionHolder = versionHolder;
    }

    /**
     * Process the given command-line arguments {@code args} with the passed {@code parser} of type {@link CommandLineParser}.
     * The parser can be acquired via the {@link CliFactory}.
     * Main logic of the parser is implemented by concrete sub classes.
     * <p>
     * The result of this method can be passed to the {@link CliExecutor}.
     *
     * @param parser the parser
     * @param args   the program's arguments
     * @return the result of the processing
     * @throws ParseException in case the command-line cannot be parsed by the given parser
     */
    @SuppressWarnings("UnusedReturnValue")
    public abstract ProcessorResult process(CommandLineParser parser, String[] args) throws ParseException;

    public CliOptionProvider getOptionProvider() {
        return optionProvider;
    }

    public abstract ProcessorResult getProcessorResult();

    public static class ProcessorResult {
        List<BdslMagicComments.Comment> magicComments;
        BDSLExecutionProperties BDSLExecutionProperties;
        File modelFile;
        File[] includeFiles;
        File[] includeUdfArchives;
        File baseOutputFolder;

        public static Builder builder() {
            return new Builder();
        }

        private ProcessorResult(List<BdslMagicComments.Comment> magicComments, BDSLExecutionProperties BDSLExecutionProperties,
                                File modelFile,
                                File[] includeFiles,
                                File[] includeUdfArchives,
                                File baseOutputFolder
        ) {
            this.magicComments = magicComments;
            this.BDSLExecutionProperties = BDSLExecutionProperties;
            this.modelFile = modelFile;
            this.includeFiles = includeFiles;
            this.includeUdfArchives = includeUdfArchives;
            this.baseOutputFolder = baseOutputFolder;
        }

        public List<BdslMagicComments.Comment> getMagicComments() {
            return magicComments;
        }

        public BDSLExecutionProperties getBDSLExecutionProperties() {
            return BDSLExecutionProperties;
        }

        public File[] getIncludeUdfArchives() {
            return includeUdfArchives;
        }

        public File getBaseOutputFolder() {
            return baseOutputFolder;
        }

        public File getModelFile() {
            return modelFile;
        }

        public File[] getIncludeFiles() {
            return includeFiles;
        }

        @SuppressWarnings("UnusedReturnValue")
        public static class Builder {
            List<BdslMagicComments.Comment> magicComments = new ArrayList<>();
            BDSLExecutionProperties bdslExecutionProperties = new BDSLExecutionProperties();
            File modelFile;
            File baseOutputFolder;
            File[] includeFiles;
            File[] includeUdfArchives;

            private Builder() {
            }

            public Builder addMagicComments(List<BdslMagicComments.Comment> magicComments) {
                this.magicComments = magicComments;
                return this;
            }

            public Builder addBdslExecutionProperties(BDSLExecutionProperties BDSLExecutionProperties) {
                this.bdslExecutionProperties = BDSLExecutionProperties;
                return this;
            }

            public Builder addIncludeFiles(File[] includeFiles) {
                this.includeFiles = includeFiles;
                return this;
            }

            public Builder addUdfFiles(File[] includeFiles) {
                this.includeUdfArchives = includeFiles;
                return this;
            }

            public Builder addModelFile(File modelFile) {
                this.modelFile = modelFile;
                return this;
            }

            public Builder addBaseOutputFolder(File folder) {
                this.baseOutputFolder = folder;
                return this;
            }

            public ProcessorResult build() {
                return new ProcessorResult(magicComments, bdslExecutionProperties, modelFile, includeFiles, includeUdfArchives,
                        baseOutputFolder);
            }

        }
    }
}
