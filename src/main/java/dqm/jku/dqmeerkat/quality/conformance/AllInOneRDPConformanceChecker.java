package dqm.jku.dqmeerkat.quality.conformance;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;

import java.io.IOException;

/**
 * Class for checking the conformance of a DP to its RDP in a generic way.
 * Holds also helper methods for plotting the conformance report statistics.
 *
 * @author lisa
 */
public class AllInOneRDPConformanceChecker extends AbstractConformanceChecker {

    private Datasource ds; // DSDElement, for which the RDP conformance should be checked
    private DSConnector conn;
    private int rdpSize;

    public AllInOneRDPConformanceChecker() {
        super(.1, 0);
        this.rdpSize = 0;
    }

    public AllInOneRDPConformanceChecker(Datasource ds, DSConnector conn, int batchSize, double threshold) {
        super(threshold, batchSize);
        this.ds = ds;
        this.conn = conn;

    }

    /**
     * Method to actually run through all batches and collect conformance statistics.
     * Separate method because it can take some while.
     *
     * @throws NoSuchMethodException
     * @throws IOException
     */
    public void runConformanceCheck() throws NoSuchMethodException, IOException {
        int noRecs = 0;
        int offset = 0;
        for (Concept c : ds.getConcepts()) {
            noRecs = conn.getNrRecords(c);
            if (noRecs < offset + batchSize)
                throw new IllegalArgumentException("Input file " + ds.getLabel() + "  has only " + noRecs + " records, which is too little for batch " + (offset + batchSize));
            for (offset = rdpSize + 1; offset + batchSize < noRecs; offset += batchSize) {
//	    		if(offset+batchSize > noRecs) batchSize = noRecs-offset;	// currently, the very last batch is not used since it contains less records than the others
                RecordList rs = conn.getPartialRecordList(c, offset, batchSize);
                for (Attribute a : c.getSortedAttributes()) {
                    if (a.hasProfile()) {
                        // generate current DP and store to list
                        DataProfile dp = a.createDataProfile(rs);
                        updateCounters(dp, a);
                    }
                }
            }
        }
    }

    /**
     * TODO
     * <p>Creates conformance report ("How compliant is my data in regards to reference data profile")</p>
     * <p>Question is where and how this report is generated and presented. Smack the user in the face with everything
     * or a more highlevel approach with not enough data</p>
     *
     * @return non persisted csv string.
     */
    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(ds.getLabel()).append("\n");
        // Add header line
        sb.append("Concept,Attribute,RDP Conformance\n");
        for (Concept c : ds.getConcepts()) {
            for (Attribute a : c.getSortedAttributes()) {
                sb.append(c.getLabel()).append(",").append(a.getLabel()).append(",");
                if (confCounter.size() > 0 && totalCounter.size() > 0) {
                    sb.append(confCounter.get(a.getURI()) / (double) totalCounter.get(a.getURI()));
                } else {
                    sb.append("NaN");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
