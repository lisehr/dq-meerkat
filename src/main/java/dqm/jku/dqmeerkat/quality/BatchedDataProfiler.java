package dqm.jku.dqmeerkat.quality;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.config.DataProfileConfiguration;
import lombok.SneakyThrows;

/**
 * <h2>BatchedDataProfiler</h2>
 * <summary>
 * Profiler implementation for batched data. Creates a {@link DataProfileCollection} for the given {@link Concept}.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.04.2022
 */
public class BatchedDataProfiler extends DataProfiler {
    public BatchedDataProfiler(Datasource ds, DSConnector conn, int batchSize, String uri,
                               DataProfileConfiguration configuration) {
        super(ds, conn, batchSize, uri, configuration);
    }

    @SneakyThrows
    @Override
    public DataProfileCollection generateProfileStep(Concept concept, int profileOffset) {
        RecordList rs = conn.getPartialRecordList(concept, profileOffset, batchSize);
        var dataProfileCollection = new DataProfileCollection(this.uri);
        for (Attribute a : concept.getSortedAttributes()) {
            if (a.hasProfile()) {
                dataProfileCollection.addDataProfile(a.createDataProfile(rs)); // TODO get datasource config in here!
            }
        }
        return dataProfileCollection;
    }
}
