package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.pattern.ledcpi.LEDCPIPatternRecognition;

import java.nio.file.Path;
import java.util.List;

/**
 * <h2>LEDCPIGenerator</h2>
 * <summary>Generator for building a single {@link LEDCPIPatternRecognition} instance. Should be combined
 * with other {@link DataProfileSkeletonGenerator}</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 14.06.2022
 */
public class LEDCPIGenerator extends DataProfileSkeletonGenerator {
    private final String propertyName;
    private final String filepath;

    public LEDCPIGenerator(String propertyName, String filepath) {
        this.propertyName = propertyName;
        this.filepath = filepath;
    }

    @Override
    protected List<ProfileStatistic<?, ?>> generateStatistics(DataProfile profile) {
        Path path = Path.of(filepath);
        return List.of(new LEDCPIPatternRecognition<>(profile, propertyName, path, Double.class)
//                ,new LEDCPIPatternRecognition<>(profile, propertyName, path, String.class)
        );

    }
}
