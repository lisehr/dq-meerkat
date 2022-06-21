package dqm.jku.dqmeerkat.quality.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;
import lombok.Getter;
import science.aist.seshat.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <h2>DataProfileConfiguration</h2>
 * <summary>Configuration class for {@link dqm.jku.dqmeerkat.quality.DataProfile}. Contains necessary
 * information such as how they look like in terms of selected
 * {@link dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic}s</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 14.06.2022
 */
public class DataProfileConfiguration {

    private static final Logger LOGGER = Logger.getInstance();

    private static DataProfileConfiguration instance;

    public static DataProfileConfiguration getInstance() {
        if (instance == null) {
            instance = loadConfig();
        }
        return instance;
    }

    /**
     * loads the external config definition and creates the singleton instance.
     *
     * @param configFile the config file containing a array of {@link ConfigComponent}s to be deserialized
     * @return A valid DataProfileConfiguration based on the given jsonString
     */
    private static DataProfileConfiguration loadConfig(File configFile) {
        var objectMapper = new ObjectMapper();
        try {
            List<ConfigComponent> components = objectMapper.readValue(configFile,
                    // fix type for deserialization. Java generics are a bit weird
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ConfigComponent.class));

            return new DataProfileConfiguration(buildGenerators(components));
        } catch (IOException e) {
            throw new RuntimeException("Could not deserialize config. Check location and content of dqConfig", e);
        }
    }

    /**
     * Loads a configuration encoded in the jsonString. Main focus of this method is testing and debugging.
     *
     * @param jsonString JSON string containing an array of {@link ConfigComponent}s to be deserialized
     * @return A valid {@link DataProfileConfiguration} based on the given jsonString
     */
    private static DataProfileConfiguration loadConfig(String jsonString) {
        var objectMapper = new ObjectMapper();
        try {
            List<ConfigComponent> components = objectMapper.readValue(jsonString,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ConfigComponent.class));
            return new DataProfileConfiguration(buildGenerators(components));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not deserialize " + jsonString, e);
        }
    }

    private static List<DataProfileSkeletonGenerator> buildGenerators(List<ConfigComponent> components) {
        return null;
    }

    /**
     * loads the external config definition and creates the singleton instance. This method assumes there exists a
     * config at {@code src/main/resource/dqConfig.json}.
     *
     * @return the singleton instance of the configuration
     */
    private static DataProfileConfiguration loadConfig() {
        return loadConfig(new File("src/main/resource/dqConfig.json"));
    }


    private DataProfileConfiguration(List<DataProfileSkeletonGenerator> generators) {
        this.generators = generators;
    }

    @Getter
    private final List<DataProfileSkeletonGenerator> generators;


}
