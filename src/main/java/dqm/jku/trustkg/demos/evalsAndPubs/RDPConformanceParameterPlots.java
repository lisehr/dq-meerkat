package dqm.jku.trustkg.demos.evalsAndPubs;

import java.io.IOException;
import java.util.HashMap;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.RDPConformanceChecker;
import dqm.jku.trustkg.util.Constants;

/**
 * This class is to evaluate the extent to which the Tributech data loaded in batches of 1,000 records adheres to the RDP boundaries.
 * Threshold: 95 % (0.95)
 * 
 * 
 * @author lisa
 *
 */
public class RDPConformanceParameterPlots {
	private static final double THRESHOLD = 0.1;		// Threshold indicates allowed deviation from reference value in percent
	private static final int RDP_SIZE = 1000;
	private static final int BATCH_SIZE = 1000;		// Set to 1 to simulate streaming data

	private static HashMap<Datasource, ConnectorCSV> dss = new HashMap<Datasource, ConnectorCSV>();

	public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
		loadDataSets();
		
		// Parameters to test
		double[] thresholds = {0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5};
		int[] rdpSize = {100, 200, 500, 1000, 2000, 3000, 5000};
		int[] batchSize = {1, 2, 5, 10, 50, 100, 1000, 2000};
		
		
		// Execute methods
		evaluateThreshold(thresholds);
	}

	private static void loadDataSets() throws IOException {
		String[] fileNames = { "Acceleration breaking or forward", "Acceleration side to side", "Acceleration up or down", "Device Voltage", "Engine RPM", "Engine Speed", "Engine Temperature" };
		for (String fname : fileNames) {
			String fpath = Constants.RESOURCES + "csv/Telematic Device Report - " + fname + ".csv";
			ConnectorCSV conn = new ConnectorCSV(fpath, ",", "\n", fname, true);
			Datasource ds = conn.loadSchema();
			dss.put(ds, conn);
		}
	}

	private static void evaluateThreshold(double[] thresholds) throws IOException, NoSuchMethodException {
		// Initialization of RDPs
		StringBuilder sb = new StringBuilder();
		
		for (Datasource ds : dss.keySet()) {
			ConnectorCSV conn = dss.get(ds);
			for (Concept c : ds.getConceptsAndAssociations()) {
				sb.append(c.getLabel());
				
				// Initialize RDPs
				RecordList rs = conn.getPartialRecordList(c, 0, RDP_SIZE);
				Attribute a = c.getAttribute("value");
				a.annotateProfile(rs);
				
				// Continuous generation of DPs and conformance checking
				for(double t : thresholds) {
					RDPConformanceChecker confChecker = new RDPConformanceChecker(ds, conn, RDP_SIZE, BATCH_SIZE, t);
					confChecker.run();
					double val = confChecker.getConformanceValue(a);
					sb.append("," + val);
				}
				sb.append("\n");
			}
		}
		
		System.out.println(sb.toString());
	}
}
