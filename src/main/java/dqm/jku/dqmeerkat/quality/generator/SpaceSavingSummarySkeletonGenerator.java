package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary.SpaceSavingSummaryProfileStatistic;

import java.util.List;

/**
 * <h2>SpaceSavingSummarySkeletonGenerator</h2>
 * <summary>
 * {@link DataSummarySkeletonGenerator} implementation for {@link SpaceSavingSummaryProfileStatistic}s.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 07.07.2022
 */
public class SpaceSavingSummarySkeletonGenerator extends DataSummarySkeletonGenerator {
    public SpaceSavingSummarySkeletonGenerator(int k) {
        super(k);
    }

    @Override
    protected List<AbstractProfileStatistic> generateStatistics(DataProfile profile) {
        return List.of(new SpaceSavingSummaryProfileStatistic(profile, k));
    }
}
