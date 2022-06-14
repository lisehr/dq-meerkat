package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.pattern.ledcpi.LEDCPIPatternRecognition;

import java.nio.file.Path;
import java.util.List;

/**
 * <h2>LEDCPIGenerator</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 14.06.2022
 */
public class LEDCPIGenerator extends DataProfileSkeletonGenerator {
    private final String propertyName;
    private final String filepath;

    public LEDCPIGenerator(DSDElement element, String propertyName, String filepath) {
        super(element);
        this.propertyName = propertyName;
        this.filepath = filepath;
    }

    @Override
    protected List<ProfileStatistic> generateStatistics(DataProfile profile) {
        return List.of(new LEDCPIPatternRecognition(profile, propertyName, Path.of(filepath)));

    }
}
