package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.*;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.*;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.dependency.KeyCandidate;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.histogram.Histogram;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>FullSkeletonGenerator</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 17.05.2022
 */
public class FullSkeletonGenerator extends DataProfileSkeletonGenerator {
    public FullSkeletonGenerator(DSDElement element) {
        super(element);
    }

    @Override
    protected List<ProfileStatistic> generateStatistics(DataProfile profile) {
        var statistics = new ArrayList<ProfileStatistic>();
        ProfileStatistic size = new NumRows(profile);
        statistics.add(size);
        ProfileStatistic min = new Minimum(profile);
        statistics.add(min);
        ProfileStatistic max = new Maximum(profile);
        statistics.add(max);
        ProfileStatistic avg = new Average(profile);
        statistics.add(avg);
        ProfileStatistic med = new Median(profile);
        statistics.add(med);
        ProfileStatistic card = new Cardinality(profile);
        statistics.add(card);
        ProfileStatistic uniq = new Uniqueness(profile);
        statistics.add(uniq);
        ProfileStatistic nullVal = new NullValues(profile);
        statistics.add(nullVal);
        ProfileStatistic nullValP = new NullValuesPercentage(profile);
        statistics.add(nullValP);
        ProfileStatistic hist = new Histogram(profile);
        statistics.add(hist);
        ProfileStatistic digits = new Digits(profile);
        statistics.add(digits);
        ProfileStatistic isCK = new KeyCandidate(profile);
        statistics.add(isCK);
        ProfileStatistic decimals = new Decimals(profile);
        statistics.add(decimals);
        ProfileStatistic basicType = new BasicType(profile);
        statistics.add(basicType);
        ProfileStatistic dataType = new DataType(profile);
        statistics.add(dataType);
        // experimental metrics
        ProfileStatistic standardDev = new StandardDeviation(profile);
        statistics.add(standardDev);
        ProfileStatistic mediAbsDevMetric = new MedianAbsoluteDeviation(profile);
        statistics.add(mediAbsDevMetric);
        return statistics;
    }

}
