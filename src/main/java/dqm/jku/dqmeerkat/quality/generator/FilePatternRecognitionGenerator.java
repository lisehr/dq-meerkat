package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.pattern.PatternRecognition;

import java.util.List;

/**
 * <h2>FilePatternRecognitionGenerator</h2>
 * <summary>
 * {@link DataProfileSkeletonGenerator} implementation for generating {@link PatternRecognition} statistics based
 * on regex definitions in the given filepath.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 03.06.2022
 */
public class FilePatternRecognitionGenerator extends DataProfileSkeletonGenerator {
    private final String filepath;

    public FilePatternRecognitionGenerator( String filepath) {
        this.filepath = filepath;
    }

    @Override
    protected List<ProfileStatistic<?, ?>> generateStatistics(DataProfile profile) {
        return List.of(new PatternRecognition(profile, filepath));
    }
}
