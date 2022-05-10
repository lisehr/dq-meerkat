package dqm.jku.dqmeerkat.quality.conformance;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2>AbstractConformanceChecker</h2>
 * <summary>Abstract Baseclass for ConformanceCheckers. Provides some handling for confidence values and generation,
 * that can be overridden if necessary</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.04.2022
 */
public abstract class AbstractConformanceChecker implements RDPConformanceChecker {
    protected final double threshold;
    protected final int batchSize;
    protected final Map<String, Integer> totalCounter = new HashMap<>();        // counts all checked DPs per attribute
    protected final Map<String, Double> confCounter = new HashMap<>();          // counts all conforming DPs per attribute

    public AbstractConformanceChecker(double threshold, int batchSize) {
        this.threshold = threshold;
        this.batchSize = batchSize;
    }

    protected void updateCounters(DataProfile profile, Attribute attribute) {
        var key = attribute.getURI();
        Integer cnt = totalCounter.get(key);
        if (cnt == null) {
            cnt = 0;
            totalCounter.put(key, cnt);
            confCounter.put(key, (double) cnt);
        }
        totalCounter.put(attribute.getURI(), ++cnt);
        double confVal = confCounter.get(key);
        var currentConformance = conformsToRDP(attribute, profile);
        confVal += currentConformance;
        confCounter.put(attribute.getURI(), confVal);
    }

    protected double conformsToRDP(Attribute a, DataProfile dp) {
        DataProfile rdp = a.getProfile();

        int conf = 0;

        List<ProfileStatistic> profileStatistics;
        if (batchSize != 1) {
            profileStatistics = rdp.getNonDependentStatistics();
        } else {
            profileStatistics = rdp.getNonAggregateStatistics();
        }

        for (ProfileStatistic rdpMetric : profileStatistics) {
            if (rdpMetric.checkConformance(dp.getStatistic(rdpMetric.getTitle()), threshold))
                conf++;
        }

        return conf / (double) profileStatistics.size();
    }

}
