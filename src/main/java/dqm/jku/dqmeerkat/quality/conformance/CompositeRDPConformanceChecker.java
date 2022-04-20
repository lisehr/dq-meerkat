package dqm.jku.dqmeerkat.quality.conformance;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.DataProfileCollection;
import dqm.jku.dqmeerkat.quality.DataProfiler;
import dqm.jku.dqmeerkat.quality.TributechDataProfiler;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2>CompositeRDPConformanceChecker</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.04.2022
 */
public class CompositeRDPConformanceChecker implements RDPConformanceChecker {

    private final DataProfiler profiler;
    private final double threshold;

    private final Map<String, Integer> totalCounter;        // counts all checked DPs per attribute
    private final Map<String, Double> confCounter;        // conts all conforming DPs per attribute


    public CompositeRDPConformanceChecker(double threshold, Datasource ds, DSConnector conn, int batchSize, String uri) {
        this(threshold, new TributechDataProfiler(ds, conn, batchSize, uri));
    }

    public CompositeRDPConformanceChecker(double threshold, DataProfiler profiler) {
        this.threshold = threshold;
        this.profiler = profiler;
        totalCounter = new HashMap<>();
        confCounter = new HashMap<>();

    }

    @Override
    public void runConformanceCheck() {
        var profiles = profiler.generateProfiles();
        var rdp = profiles.stream().findFirst().orElseThrow();
        profiles.stream().skip(1).forEach(dataProfileCollection -> {
            for (DataProfile profile : dataProfileCollection.getProfiles()) {
                Attribute attribute = (Attribute) profile.getElem();
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
                System.out.println("Conformance= " + currentConformance);
                confVal += currentConformance;
                confCounter.put(attribute.getURI(), confVal);
            }
        });

    }

    @Override
    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(profiler.getUri()).append("\n");
        // Add header line
        sb.append("Concept,Attribute,RDP Conformance\n");
        profiler.getDataProfiles().stream()
                .map(DataProfileCollection::getProfiles)
                .flatMap(dataProfiles -> dataProfiles.stream()
                        .map(dataProfile -> (Attribute) dataProfile.getElem()))
                .forEach(attribute -> {
                    sb.append(profiler.getUri()).append(",").append(attribute.getLabel()).append(",");
                    if (confCounter.size() > 0 && totalCounter.size() > 0) {
                        sb.append(confCounter.get(attribute.getURI()) / (double) totalCounter.get(attribute.getURI()));
                    } else {
                        sb.append("NaN");
                    }
                    sb.append("\n");
                });

        return sb.toString();
    }

    private double conformsToRDP(Attribute a, DataProfile dp) {
        DataProfile rdp = a.getProfile();

        int conf = 0;

        List<ProfileStatistic> mlist;
        if (this.profiler.getBatchSize() != 1) {
            mlist = rdp.getNonDependentStatistics();
        } else {
            mlist = rdp.getNonAggregateStatistics();
        }

        for (ProfileStatistic rdpMetric : mlist) {
            if (rdpMetric.checkConformance(dp.getStatistic(rdpMetric.getTitle()), threshold))
                conf++;
        }

        return conf / (double) mlist.size();
    }
}
