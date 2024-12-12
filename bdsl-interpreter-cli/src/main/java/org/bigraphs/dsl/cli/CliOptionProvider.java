package org.bigraphs.dsl.cli;

import org.bigraphs.dsl.cli.configuration.BDSLExecutionProperties;
import org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.List;

/**
 * @author Dominik Grzelak
 */
public abstract class CliOptionProvider {
    public static String DEFAULT_BDSL_SPECIFICATION_FILE = "bdsl-execution.properties";

    protected BDSLExecutionProperties bdslExecutionProperties;

    public CliOptionProvider(BDSLExecutionProperties bdslExecutionProperties) {
        this.bdslExecutionProperties = bdslExecutionProperties;
    }

    public ModelCheckingOptions getModelCheckingOptions() {
        return bdslExecutionProperties.getModelCheckingOptions();
    }

    public BDSLExecutionProperties getBdslExecutionProperties() {
        return bdslExecutionProperties;
    }

    public abstract List<Option> getOptionElements();

    public abstract Options getOptions();
}
