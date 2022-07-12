package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.*;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.*;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.dependency.KeyCandidate;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.histogram.Histogram;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>FullSkeletonGenerator</h2>
 * <summary>
 * Generates all currently defined {@link AbstractProfileStatistic}s for the given {@link DataProfile} and
 * returns them
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 17.05.2022
 */
public class FullSkeletonGenerator extends DataProfileSkeletonGenerator{
    @Override
    protected List<ProfileStatistic<?>> generateStatistics(DataProfile profile) {
        List<ProfileStatistic<?>> statistics = new ArrayList<>();
        ProfileStatistic<?> size = new NumRows(profile);
        statistics.add(size);
        var min = new Minimum(profile);
        statistics.add(min);
        var max = new Maximum(profile);
        statistics.add(max);
        var avg = new Average(profile);
        statistics.add(avg);
        var med = new Median(profile);
        statistics.add(med);
        var card = new Cardinality(profile);
        statistics.add(card);
        var uniq = new Uniqueness(profile);
        statistics.add(uniq);
        var nullVal = new NullValues(profile);
        statistics.add(nullVal);
        var nullValP = new NullValuesPercentage(profile);
        statistics.add(nullValP);
        var hist = new Histogram(profile);
        statistics.add(hist);
        var digits = new Digits(profile);
        statistics.add(digits);
        var isCK = new KeyCandidate(profile);
        statistics.add(isCK);
        var decimals = new Decimals(profile);
        statistics.add(decimals);
        var basicType = new BasicType(profile);
        statistics.add(basicType);
        var dataType = new DataType(profile);
        statistics.add(dataType);
        // experimental metrics
        var standardDev = new StandardDeviation(profile);
        statistics.add(standardDev);
        var mediAbsDevMetric = new MedianAbsoluteDeviation(profile);
        statistics.add(mediAbsDevMetric);
        return statistics;
    }

}
