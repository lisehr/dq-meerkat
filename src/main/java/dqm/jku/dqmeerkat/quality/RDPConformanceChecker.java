package dqm.jku.dqmeerkat.quality;

import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

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
	private Map<String, Double> confCounter;		// conts all conforming DPs per attribute
	
	public RDPConformanceChecker() {
		this.rdpSize = 0;
		this.batchSize = 0;
		this.threshold = 0;
	}
	
	public RDPConformanceChecker(Datasource ds, DSConnector conn, int batchSize, double threshold) {
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
	 * @throws NoSuchMethodException 
	 * @throws IOException 
	 */
	public void run() throws NoSuchMethodException, IOException {
		int noRecs = 0;
	    int offset = 0;
	    for (Concept c : ds.getConcepts()) {
	    	noRecs = conn.getNrRecords(c);
	    	if(noRecs < offset+batchSize) throw new IllegalArgumentException("Input file " + ds.getLabel() + "  has only " + noRecs + " records, which is too little for batch " + (offset+batchSize));
	    	for(offset = rdpSize + 1; offset+batchSize < noRecs; offset += batchSize) {
//	    		if(offset+batchSize > noRecs) batchSize = noRecs-offset;	// currently, the very last batch is not used since it contains less records than the others
		    	RecordList rs = conn.getPartialRecordList(c, offset, batchSize);
		    	for(Attribute a : c.getSortedAttributes()) {
		    		if(a.hasProfile()) {
		    			// generate current DP and store to list
			    		DataProfile dp = a.createDataProfile(rs);
			    		String key = a.getURI();
			    		Integer cnt = totalCounter.get(key);
			    		if(cnt == null) {
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

	public String getReport() {
		StringBuilder sb = new StringBuilder();
		sb.append(ds.getLabel() + "\n");
		// Add header line
		sb.append("Concept,Attribute,RDP Conformance\n");
		for (Concept c : ds.getConcepts()) {
			for(Attribute a : c.getSortedAttributes()) {
	    		sb.append(c.getLabel() + "," + a.getLabel() + ",");
	    		if(confCounter.size() > 0 && totalCounter.size() > 0) {
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
		if(confCounter.size() > 0 && totalCounter.size() > 0) {
			val = confCounter.get(a.getURI()) / (double) totalCounter.get(a.getURI());
		} 
		return val;
	}
	
	private double conformsToRDP(Attribute a, DataProfile dp) {
		DataProfile rdp = a.getProfile();
		
		int conf = 0;
		
		List<ProfileStatistic> mlist = null;
		if(this.batchSize != 1) {   
			mlist =  rdp.getNonDependentStatistics();
		} else {
			mlist = rdp.getNonAggregateStatistics();
		}
		
		for(ProfileStatistic rdpMetric : mlist) {
			if(rdpMetric.checkConformance(dp.getStatistic(rdpMetric.getTitle()), threshold)) conf++;
		}
		double value = conf / (double) mlist.size();
		
		return value;
	}
}
