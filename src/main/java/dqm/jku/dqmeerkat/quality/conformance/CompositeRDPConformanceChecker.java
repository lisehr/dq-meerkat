package dqm.jku.dqmeerkat.quality.conformance;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.DataProfileCollection;
import dqm.jku.dqmeerkat.quality.DataProfiler;
import dqm.jku.dqmeerkat.quality.BatchedDataProfiler;
import dqm.jku.dqmeerkat.quality.config.DataProfileConfiguration;

/**
 * <h2>CompositeRDPConformanceChecker</h2>
 * <summary>Conformance Checker, that uses a {@link DataProfiler} to generate {@link DataProfile}s and evaluate them
 * against the (already initialised) RDP of the given {@link Attribute}s.</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.04.2022
 */
public class CompositeRDPConformanceChecker extends AbstractConformanceChecker {

    private final DataProfiler profiler;




    public CompositeRDPConformanceChecker(double threshold, Datasource ds, DSConnector conn, int batchSize, String uri,
                                          DataProfileConfiguration configuration) {
        this(threshold, new BatchedDataProfiler(ds, conn, batchSize, uri, configuration));
    }

    public CompositeRDPConformanceChecker(double threshold, DataProfiler profiler) {
        super(threshold, profiler.getBatchSize());
        this.profiler = profiler;

    }

    @Override
    public void runConformanceCheck() {
        var profiles = profiler.generateProfiles();
        var rdp = profiles.stream().findFirst().orElseThrow();
        profiles.stream().skip(1).forEach(dataProfileCollection -> {
            for (DataProfile profile : dataProfileCollection.getProfiles()) {
                Attribute attribute = (Attribute) profile.getElem();
                updateCounters(profile, attribute);
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


}
