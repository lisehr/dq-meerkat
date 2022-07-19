package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary.MGSummaryProfileStatistic;

import java.util.List;

/**
 * <h2>DataSummarySkeletonGenerator</h2>
 * <summary>
 * {@link DataSummarySkeletonGenerator} implementation for {@link MGSummaryProfileStatistic}s.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
public class MGSummarySkeletonGenerator extends DataSummarySkeletonGenerator {


    public MGSummarySkeletonGenerator(int k) {
        super(k);
    }

    @Override
    protected List<ProfileStatistic<?, ?>> generateStatistics(DataProfile profile) {
        return List.of(new MGSummaryProfileStatistic(profile, k));
    }
}
