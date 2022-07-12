package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
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
    protected List<AbstractProfileStatistic> generateStatistics(DataProfile profile) {
        var statistics = new ArrayList<AbstractProfileStatistic>();
        AbstractProfileStatistic size = new NumRows(profile);
        statistics.add(size);
        AbstractProfileStatistic min = new Minimum(profile);
        statistics.add(min);
        AbstractProfileStatistic max = new Maximum(profile);
        statistics.add(max);
        AbstractProfileStatistic avg = new Average(profile);
        statistics.add(avg);
        AbstractProfileStatistic med = new Median(profile);
        statistics.add(med);
        AbstractProfileStatistic card = new Cardinality(profile);
        statistics.add(card);
        AbstractProfileStatistic uniq = new Uniqueness(profile);
        statistics.add(uniq);
        AbstractProfileStatistic nullVal = new NullValues(profile);
        statistics.add(nullVal);
        AbstractProfileStatistic nullValP = new NullValuesPercentage(profile);
        statistics.add(nullValP);
        AbstractProfileStatistic hist = new Histogram(profile);
        statistics.add(hist);
        AbstractProfileStatistic digits = new Digits(profile);
        statistics.add(digits);
        AbstractProfileStatistic isCK = new KeyCandidate(profile);
        statistics.add(isCK);
        AbstractProfileStatistic decimals = new Decimals(profile);
        statistics.add(decimals);
        AbstractProfileStatistic basicType = new BasicType(profile);
        statistics.add(basicType);
        AbstractProfileStatistic dataType = new DataType(profile);
        statistics.add(dataType);
        // experimental metrics
        AbstractProfileStatistic standardDev = new StandardDeviation(profile);
        statistics.add(standardDev);
        AbstractProfileStatistic mediAbsDevMetric = new MedianAbsoluteDeviation(profile);
        statistics.add(mediAbsDevMetric);
        return statistics;
    }

}
