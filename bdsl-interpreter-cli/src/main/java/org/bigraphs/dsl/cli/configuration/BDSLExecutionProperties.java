package org.bigraphs.dsl.cli.configuration;

import org.bigraphs.dsl.cli.configuration.v1.DefaultCliOptionProvider;
import org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * General external configuration file "holder" for the BDSL interpreter.
 * The default filename is <code>{@literal bdsl-execution.properties}</code>.
 * <p>
 * The specific model checking options are covered by the {@link ModelCheckingOptions} class.
 * <p>
 * Can also be used/replaced by the user via the filesystem next to the actual BDSL program that is going to be executed
 * by the interpreter.
 *
 * @author Dominik Grzelak
 */
@Configuration
@PropertySource(value = {"classpath:bdsl-execution.properties", "classpath:cli-test.properties"}, ignoreResourceNotFound = true)
public class BDSLExecutionProperties {
    ConcurrentMap<String, Object> properties;

    @Autowired
    protected ModelCheckingOptions options;

    @Value("${" + DefaultCliOptionProvider.Environment.OPTION_INPUT_MODEL_FILE + ":#{null}}")
    private String mainBdslFile;

    //    @Value("${" + DefaultCliOptionProvider.Environment.OPTION_INCLUDE_MODEL_FILE + ":#{null}}")
    @Value("#{'${" + DefaultCliOptionProvider.Environment.OPTION_INCLUDE_MODEL_FILE + "}'.split(',')}")
    private String[] includeBdslFile;

    @Value("${" + DefaultCliOptionProvider.Environment.OPTION_OUTPUT_FOLDER + "}")
    private String outputFolder;

    //    @Value("${" + DefaultCliOptionProvider.Environment.OPTION_UDF_ARCHIVES + ":#{null}}")
    @Value("#{'${" + DefaultCliOptionProvider.Environment.OPTION_UDF_ARCHIVES + "}'.split(',')}")
    private String[] udfArchives;

    public BDSLExecutionProperties() {
        properties = new ConcurrentHashMap<>();
    }

    public ModelCheckingOptions getModelCheckingOptions() {
        return options;
    }

    @PostConstruct
    private void postConstruct() {
        set(DefaultCliOptionProvider.Environment.OPTION_INPUT_MODEL_FILE, mainBdslFile);
        set(DefaultCliOptionProvider.Environment.OPTION_INCLUDE_MODEL_FILE, includeBdslFile);
        set(DefaultCliOptionProvider.Environment.OPTION_OUTPUT_FOLDER, outputFolder);
        set(DefaultCliOptionProvider.Environment.OPTION_UDF_ARCHIVES, udfArchives);
    }

    public <T> Optional<T> get(String key, Class<T> classType) {
        return Optional.ofNullable(classType.cast(properties.get(key)));
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(properties.get(key));
    }

    public void set(String key, Object value) {
        if (value instanceof String[] && ((String[]) value).length == 1 &&
                (Objects.isNull(((String[]) value)[0]) || ((String[]) value)[0].isEmpty())) {
            value = null;
        }
        if (Objects.nonNull(key) && Objects.nonNull(value))
            properties.put(key, value);
    }
}
