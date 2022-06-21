package dqm.jku.dqmeerkat.quality.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.FullSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.LEDCPIGenerator;
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
     * @return the singleton instance of the configuration
     */
    private static DataProfileConfiguration loadConfig() {
        var objectMapper = new ObjectMapper();
        try {
            List<ConfigComponent> components = objectMapper.readValue(new File("src/main/resource/dqConfig.json"),
                    // fix type for deserialization. Java generics are a bit weird
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ConfigComponent.class));
            // TODO define subclasses of component that correspond generators
            for (ConfigComponent component : components) {
                if (component instanceof LEDCPIConfigComponent)
                    LOGGER.info(component);
            }
            LOGGER.info(components);
        } catch (IOException e) {
            throw new RuntimeException("Could not deserialize config. Check location and content of dqConfig", e);
        }
        return new DataProfileConfiguration(List.of(
                new FullSkeletonGenerator(),
                new LEDCPIGenerator("at.fh.scch/identifier#humidity:*",
                        "src/main/resource/data/ledc-pi_definitions.json")));
    }


    private DataProfileConfiguration(List<DataProfileSkeletonGenerator> generators) {
        this.generators = generators;
    }

    @Getter
    private final List<DataProfileSkeletonGenerator> generators;


}
