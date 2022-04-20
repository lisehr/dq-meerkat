package dqm.jku.dqmeerkat.quality.conformance;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for checking the conformance of a DP to its RDP in a generic way.
 * Holds also helper methods for plotting the conformance report statistics.
 *
 * @author lisa
 */
// TODO Extend this class, add properties to get all DPs, add logic for persisting in influxdbv2
public class AllInOneRDPConformanceChecker implements RDPConformanceChecker {

    private Datasource ds; // DSDElement, for which the RDP conformance should be checked
    private DSConnector conn;
    private int rdpSize;
    private int batchSize;
    private double threshold;
    private Map<String, Integer> totalCounter;        // counts all checked DPs per attribute
    private Map<String, Double> confCounter;        // conts all conforming DPs per attribute

    public AllInOneRDPConformanceChecker() {
        this.rdpSize = 0;
        this.batchSize = 0;
        this.threshold = 0;
    }

    public AllInOneRDPConformanceChecker(Datasource ds, DSConnector conn, int batchSize, double threshold) {
        this.ds = ds;
        this.conn = conn;
        this.batchSize = batchSize;
        this.threshold = threshold;
        this.totalCounter = new HashMap<String, Integer>();
        this.confCounter = new HashMap<String, Double>();
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
        for (Concept c : ds.getConcepts()) { // TODO extract this whole loop into the main for visualisation
            noRecs = conn.getNrRecords(c);
            if (noRecs < offset + batchSize)
                throw new IllegalArgumentException("Input file " + ds.getLabel() + "  has only " + noRecs + " records, which is too little for batch " + (offset + batchSize));
            for (offset = rdpSize + 1; offset + batchSize < noRecs; offset += batchSize) {
//	    		if(offset+batchSize > noRecs) batchSize = noRecs-offset;	// currently, the very last batch is not used since it contains less records than the others
                RecordList rs = conn.getPartialRecordList(c, offset, batchSize);
                for (Attribute a : c.getSortedAttributes()) {
                    if (a.hasProfile()) {
                        // generate current DP and store to list
                        DataProfile dp = a.createDataProfile(rs); // TODO extract this dp as list of dps
                        String key = a.getURI();
                        Integer cnt = totalCounter.get(key);
                        if (cnt == null) {
                            cnt = 0;
                            totalCounter.put(key, cnt);
                            confCounter.put(key, (double) cnt);
                        }
                        totalCounter.put(a.getURI(), ++cnt);

                        double confVal = confCounter.get(key);
                        confVal += conformsToRDP(a, dp);
                        confCounter.put(a.getURI(), confVal);
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

    public double getConformanceValue(Attribute a) {
        double val = 0.0;
        if (confCounter.size() > 0 && totalCounter.size() > 0) {
            val = confCounter.get(a.getURI()) / (double) totalCounter.get(a.getURI());
        }
        return val;
    }

    private double conformsToRDP(Attribute a, DataProfile dp) {
        DataProfile rdp = a.getProfile();

        int conf = 0;

        List<ProfileStatistic> mlist = null;
        if (this.batchSize != 1) {
            mlist = rdp.getNonDependentStatistics();
        } else {
            mlist = rdp.getNonAggregateStatistics();
        }

        for (ProfileStatistic rdpMetric : mlist) {
            if (rdpMetric.checkConformance(dp.getStatistic(rdpMetric.getTitle()), threshold)) conf++;
        }

        return conf / (double) mlist.size();
    }
}
