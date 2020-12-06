package dqm.jku.trustkg.demos.repeatability;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.MetricTitle;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * This class is to evaluate the extent to which the Tributech data stream adheres to the RDP boundaries.
 * Threshold: 95 % (0.95)
 * 
 * 
 * @author lisa
 *
 */
public class RDPConformanceStreamingData {
  private static final int FILEINDEX = 7;
  private static final double THRESHOLD = 0.95;
  
  private static Map<String, Integer> validityCounter = new HashMap<String, Integer>();
  private static Map<String, Integer> totalCounter = new HashMap<String, Integer>();
  private static List<Double> exceptions = new LinkedList<Double>();

  public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
    ConnectorCSV conn = FileSelectionUtil.connectToCSV(FILEINDEX);

    Concept testCon = null;

    Datasource ds = conn.loadSchema();
    for (Concept c : ds.getConcepts()) {
      RecordList rs = conn.getPartialRecordList(c, 0, 2000);
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
        validityCounter.put(a.getURI(), 0);
        totalCounter.put(a.getURI(), 0);
      }
      testCon = c;
    }

    RecordList rs = conn.getPartialRecordList(testCon, 2001, Integer.MAX_VALUE);
    boolean fileFinished = false;
    @SuppressWarnings("unused")
	int i = 0;
    Iterator<Record> itR = rs.iterator();
    while (!fileFinished) {	
    	fileFinished = checkMeasurement(testCon, itR);
    	i++;
    }
    
    // Finally: print evaluations
    for(String key : validityCounter.keySet()) {
    	double adherence = validityCounter.get(key) / (double) totalCounter.get(key);
    	System.out.println(key + ": " + validityCounter.get(key) + " / " + totalCounter.get(key) + " = " + adherence);
		System.out.print("(" + exceptions.size() + ") Exceptions: ");
    	for(Double e : exceptions) {
    		System.out.print(e.toString() + ", ");
    	}
    }
  }

  private static boolean checkMeasurement(Concept testCon, Iterator<Record> itR) {
	  if (!itR.hasNext()) return true;
	    Record r = itR.next();
	    RecordList rs = new RecordList();
	    rs.addRecord(r);
	    
	    for(Attribute a : testCon.getSortedAttributes()) {
	    	if(conformsToRDP(rs, a)) {
		    	Integer tmp = validityCounter.get(a.getURI());
		    	validityCounter.put(a.getURI(), ++tmp);
		    }
		    Integer tmp = totalCounter.get(a.getURI());
		    totalCounter.put(a.getURI(), ++tmp);
		    return !itR.hasNext();
	    }
	    return !itR.hasNext();
//	    Attribute a = testCon.getAttribute("value");
  }
  

  public static boolean conformsToRDP(RecordList rs, Attribute a) {
//	  DataProfile profile = a.getProfile();
//	  for(ProfileMetric pm : profile.getMetrics()) {
//		  pm.checkConformance(rs, THRESHOLD);
//	  }
//	  
//	  ProfileMetric min = profile.getMetric(MetricTitle.min);
//	  ProfileMetric max = profile.getMetric(MetricTitle.max);
//
//	  Double val = (double) r.getField(a);
//	  Double minVal = (double) min.getValue();
//	  Double maxVal = (double) max.getValue();
//	  if(val >= minVal && val <= maxVal) {
//		  return true;
//	  }
//	  
//	  exceptions.add(val);
	  return false;
  }
}
