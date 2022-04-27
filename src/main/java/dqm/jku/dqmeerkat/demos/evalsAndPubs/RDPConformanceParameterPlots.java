package dqm.jku.dqmeerkat.demos.evalsAndPubs;

import java.io.IOException;
import java.util.HashMap;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.conformance.AllInOneRDPConformanceChecker;
import dqm.jku.dqmeerkat.util.Constants;

/**
 * This class is to evaluate the extent to which the Tributech data loaded in batches of 1,000 records adheres to the RDP boundaries.
 * Threshold: 95 % (0.95)
 * 
 * 
 * @author lisa
 *
 */
public class RDPConformanceParameterPlots {
	private static enum EVAL{THRESHOLD, RDP_SIZE, BATCH_SIZE};
	private static final double DEFAULT_THRESHOLD = 0.1;		// Threshold indicates allowed deviation from reference value in percent
	private static final int DEFAULT_RDP_SIZE = 1000;
	private static final int DEFAULT_BATCH_SIZE = 1000;			// Set to 1 to simulate streaming data
	
	// private static double[] thresholds = {0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5};
	private static double[] thresholds = {0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15, 0.1, 0.05, 0.0};
	private static int[] batchSize = {1, 2, 5, 10, 50, 100, 1000, 2000};
	private static int[] rdpSize = {100, 200, 500, 1000, 2000, 3000, 5000};
	
	private static HashMap<Datasource, ConnectorCSV> dss = new HashMap<Datasource, ConnectorCSV>();

	public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
		loadDataSets();
		
		// Evaluate thresholds
		evaluate(EVAL.THRESHOLD);
		
		// Evaluate thresholds batch size
		evaluate(EVAL.BATCH_SIZE);
		
		// Evaluate thresholds rdp size
		evaluate(EVAL.RDP_SIZE);
	}

	private static void loadDataSets() throws IOException {
		String[] fileNames = { "Acceleration breaking or forward", "Acceleration side to side", "Acceleration up or down", "Device Voltage", "Engine RPM", "Engine Speed"};
		// , "Engine Temperature" 
		for (String fname : fileNames) {
			String fpath = Constants.RESOURCES_FOLDER + "csv/Telematic Device Report - " + fname + ".csv";
			ConnectorCSV conn = new ConnectorCSV(fpath, ",", "\n", fname, true);
			Datasource ds = conn.loadSchema();
			dss.put(ds, conn);
		}
	}

	private static void evaluate(EVAL e) throws IOException, NoSuchMethodException {
		// Initialization of RDPs
		StringBuilder sb = new StringBuilder();
		sb.append(printHeaderLine(e));
		
		for (Datasource ds : dss.keySet()) {
			ConnectorCSV conn = dss.get(ds);
			for (Concept c : ds.getConceptsAndAssociations()) {
				sb.append(c.getLabel());
				
				// Initialize RDPs
				if(e == EVAL.RDP_SIZE) {
					for(int rdpS : rdpSize) {
						RecordList rs = conn.getPartialRecordList(c, 0, rdpS);
						Attribute a = c.getAttribute("value");
						a.annotateProfile(rs);
						
						String s = checkConformance(ds, conn, a, DEFAULT_BATCH_SIZE, DEFAULT_THRESHOLD);
						sb.append(s);
					}
				} else {
					RecordList rs = conn.getPartialRecordList(c, 0, DEFAULT_RDP_SIZE);
					Attribute a = c.getAttribute("value");
					a.annotateProfile(rs);
					
					// Continuous generation of DPs and conformance checking
					if(e == EVAL.THRESHOLD) {
						for(double t : thresholds) {
							String s = checkConformance(ds, conn, a, DEFAULT_BATCH_SIZE, t);
							sb.append(s);
						}
					} else if (e == EVAL.BATCH_SIZE) {
						for(int bs : batchSize) {
							String s = checkConformance(ds, conn, a, bs, DEFAULT_THRESHOLD);
							sb.append(s);
						}
					} else {
						String s = checkConformance(ds, conn, a, DEFAULT_BATCH_SIZE, DEFAULT_THRESHOLD);
						sb.append(s);
					}
				}
				sb.append("\n");
			}
		}
		sb.append("\n");
		System.out.println(sb.toString());
	}

	private static String checkConformance(Datasource ds, ConnectorCSV conn, Attribute a, int batchSize, double threshold) throws NoSuchMethodException, IOException {
		AllInOneRDPConformanceChecker confChecker = new AllInOneRDPConformanceChecker(ds, conn, batchSize, threshold);
		double val = Double.NaN;
		try {
			confChecker.runConformanceCheck();
			// TODO Fix the missing method below!!!
//			val = confChecker.getConformanceValue(a);
		} catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		return("," + val);
	}
	
	
	private static String printHeaderLine(EVAL e) {
		StringBuilder sb = new StringBuilder();
		if(e == EVAL.THRESHOLD) {
			sb.append("THRESHOLDS"); 
			for(double t : thresholds) {
				sb.append("," + t);
			}
		} else if (e == EVAL.BATCH_SIZE) {
			sb.append("BATCH_SIZE"); 
			for(int bs : batchSize) {
				sb.append("," + bs);
			}
		} else {
			sb.append("RDP_SIZE"); 
			for(int rs : rdpSize) {
				sb.append("," + rs);
			}
		}
		sb.append("\n");
		return sb.toString();
	}
}
