package dqm.jku.dqmeerkat.quality;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>DataProfiler</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.04.2022
 */
public abstract class DataProfiler {
    protected final String uri;
    protected Datasource ds; // DSDElement, for which the RDP conformance should be checked
    protected DSConnector conn;
    protected int batchSize;

    public DataProfiler(Datasource ds, DSConnector conn, int batchSize, String uri) {
        this.ds = ds;
        this.conn = conn;
        this.batchSize = batchSize;
        this.uri = uri;
    }

    public DataProfiler(Datasource ds, DSConnector conn, int batchSize) {
        this(ds, conn, batchSize, "http:/example.com/");
    }

    public abstract DataProfileCollection generateProfileStep(Concept concept, int profileOffset);

    // TODO use this method in the main for visualisation
    @SneakyThrows
    public List<DataProfileCollection> generateProfiles() {
        int offset = 0;
        List<DataProfileCollection> dataProfiles = new ArrayList<>();
        for (Concept c : ds.getConcepts()) {
            var noRecs = conn.getNrRecords(c);
            if (noRecs < offset + batchSize)
                throw new IllegalArgumentException("Input file " + ds.getLabel() + "  has only " + noRecs + " records, which is too little for batch " + (offset + batchSize));
            for (offset = 1; offset + batchSize < noRecs; offset += batchSize) {
//	    		if(offset+batchSize > noRecs) batchSize = noRecs-offset;	// currently, the very last batch is not used since it contains less records than the others
                dataProfiles.add(generateProfileStep(c, offset));
            }
        }
        return dataProfiles;
    }

}
