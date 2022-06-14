package dqm.jku.dqmeerkat.quality.config;

import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.FullSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.LEDCPIGenerator;
import lombok.Getter;

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

    private static DataProfileConfiguration instance;

    private DataProfileConfiguration(List<DataProfileSkeletonGenerator> generators) {
        this.generators = generators;
    }


    public static DataProfileConfiguration getInstance() {
        if (instance == null) {

            instance = loadConfig();
        }

        return instance;
    }

    /**
     * lodas the external config definition and creates the singleton instance.
     *
     * @return the singleton instance of the configuration
     */
    private static DataProfileConfiguration loadConfig() {
        // TODO define json file loading
        return new DataProfileConfiguration(List.of(
//                new FullSkeletonGenerator(),
                new LEDCPIGenerator("at.fh.scch/identifier#humidity:*",
                        "src/main/resource/data/ledc-pi_definitions.json")));
    }

    @Getter
    private final List<DataProfileSkeletonGenerator> generators;
}
