package dqm.jku.trustkg.quality;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.profilingmetrics.MetricTitle;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

/**
 * Class for checking the conformance of a DP to its RDP in a generic way.
 * Holds also helper methods for plotting the conformance report statistics.
 * 
 * @author lisa
 *
 */
public class RDPConformanceChecker {
	
	private Datasource ds; // DSDElement, for which the RDP conformance should be checked
	private DSConnector conn;
	private int rdpSize;
	private int batchSize;
	private double threshold;
	private Map<String, Integer> totalCounter;		// counts all checked DPs per attribute
	private Map<String, Integer> confCounter;		// conts all conforming DPs per attribute
	
	public RDPConformanceChecker() {
		this.rdpSize = 0;
		this.batchSize = 0;
		this.threshold = 0;
	}
	
	public RDPConformanceChecker(Datasource ds, DSConnector conn, int rdpSize, int batchSize, double threshold) {
		this.ds = ds;
		this.conn = conn;
		this.rdpSize = rdpSize;
		this.batchSize = batchSize;
		this.threshold = threshold;
		this.totalCounter = new HashMap<String, Integer>();
		this.confCounter = new HashMap<String, Integer>();
	}
	
	/**
	 * Method to actually run through all batches and collect conformance statistics.
	 * Separate method because it can take some while.
	 * @throws NoSuchMethodException 
	 * @throws IOException 
	 */
	public void run() throws NoSuchMethodException, IOException {
		int noRecs = 0;
	    int offset = 0;
	    for (Concept c : ds.getConcepts()) {
	    	noRecs = conn.getNrRecords(c);
	    	for(offset = rdpSize + 1; offset < noRecs; offset += batchSize) {
		    	RecordList rs = conn.getPartialRecordList(c, offset, batchSize);
		    	for(Attribute a : c.getSortedAttributes()) {
		    		// generate current DP and store to list
		    		DataProfile dp = a.createDataProfile(rs);
		    		String key = a.getURI();
		    		Integer cnt = totalCounter.get(key);
		    		if(cnt == null) {
		    			cnt = 0;
		    			totalCounter.put(key, cnt);
		    			confCounter.put(key, cnt);
		    		}
		    		totalCounter.put(a.getURI(), ++cnt);
		    		
		    		if(conformsToRDP(a, dp)) {
		    			cnt = confCounter.get(key);
			    		confCounter.put(a.getURI(), ++cnt);
		    		}
		    	}
		    }
	    }
	}

	public String getReport() {
		StringBuilder sb = new StringBuilder();
		sb.append(ds.getLabel() + "\n");
		// Add header line
		sb.append("Concept,Attribute,RDP Conformance\n");
		for (Concept c : ds.getConcepts()) {
			for(Attribute a : c.getSortedAttributes()) {
	    		sb.append(c.getLabel() + "," + a.getLabel() + ",");
	    		sb.append(confCounter.get(a.getURI()) / (double) totalCounter.get(a.getURI()));
	    		sb.append("\n");
	    	}
	    }
		return sb.toString();
	}
	
	private boolean conformsToRDP(Attribute a, DataProfile dp) {
		DataProfile rdp = a.getProfile();
		
//		for(ProfileMetric rdpMetric : rdp.getMetrics()) {
//			rdpMetric.checkConformance(dp.getMetric(rdpMetric.getTitle()), threshold);
//		}
		
		System.out.println(a.getLabel());
		ProfileMetric rdpMetric = rdp.getMetric(MetricTitle.card);
		ProfileMetric dpMetric = dp.getMetric(MetricTitle.card);
		boolean conformance = rdpMetric.checkConformance(dpMetric, threshold);
		return conformance;
	}
}
