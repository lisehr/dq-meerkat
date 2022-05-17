package dqm.jku.dqmeerkat.quality;

import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import java.util.List;

/**
 * <h2>DefaultSkeletonGenerator</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 17.05.2022
 */
public class DefaultSkeletonGenerator extends DataProfileSkeletonGenerator{
    public DefaultSkeletonGenerator(DSDElement element) {
        super(element);
    }

    @Override
    protected List<ProfileStatistic> generateStatistics(DataProfile profile) {
        return null;
    }
}
