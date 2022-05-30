package dqm.jku.dqmeerkat.quality;

import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.multicolumn.outliers.IsolationForest;
import dqm.jku.dqmeerkat.quality.profilingstatistics.multicolumn.outliers.IsolationForestPercentage;
import dqm.jku.dqmeerkat.quality.profilingstatistics.multicolumn.outliers.LocalOutlierFactor;
import dqm.jku.dqmeerkat.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>IsolationForestSkeletonGenerator</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 30.05.2022
 */
public class IsolationForestSkeletonGenerator extends DataProfileSkeletonGenerator {
    public IsolationForestSkeletonGenerator(DSDElement element) {
        super(element);
    }

    // As Isolation Forest and IsolationForestPercentage are dependant on JEP, only run them when it is enabled!
    @Override
    protected boolean checkValidity() {
        return element instanceof Concept &&
                Constants.ENABLE_JEP;
    }

    @Override
    protected List<ProfileStatistic> generateStatistics(DataProfile profile) {
        List<ProfileStatistic> statistics = new ArrayList<>();
        ProfileStatistic isoFor = new IsolationForest(profile);
        statistics.add(isoFor);
        ProfileStatistic isoForPer = new IsolationForestPercentage(profile);
        statistics.add(isoForPer);
        ProfileStatistic lof = new LocalOutlierFactor(profile);
        statistics.add(lof);
        return statistics;
    }
}
