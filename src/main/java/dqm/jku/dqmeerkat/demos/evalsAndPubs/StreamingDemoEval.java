package dqm.jku.dqmeerkat.demos.evalsAndPubs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

/**
 * This class is to evaluate how many records are within the RDP boundaries for the streaming demo.
 * 
 * 
 * @author optimusseptim, lisa
 *
 */
public class StreamingDemoEval {
  private static final int FILEINDEX = 9;
  
  private static Map<String, Integer> validityCounter = new HashMap<String, Integer>();
  private static Map<String, Integer> totalCounter = new HashMap<String, Integer>();
  private static List<Double> exceptions = new LinkedList<Double>();

  public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
    ConnectorCSV conn = FileSelectionUtil.connectToCSV(FILEINDEX);

    Concept testCon = null;

    Datasource ds = conn.loadSchema();
    for (Concept c : ds.getConcepts()) {
      RecordList rs = conn.getPartialRecordList(c, 0, 5000);
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
      }
      testCon = c;
      validityCounter.put(testCon.getLabel(), 0);
      totalCounter.put(testCon.getLabel(), 0);
    }

    RecordList rs = conn.getPartialRecordList(testCon, 5001, Integer.MAX_VALUE);
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
	    Attribute a = testCon.getAttribute("value");
	    if(strictlyAdheresToRDP(r, a)) {
	    	Integer tmp = validityCounter.get(testCon.getLabel());
	    	validityCounter.put(testCon.getLabel(), ++tmp);
	    }
	    Integer tmp = totalCounter.get(testCon.getLabel());
	    totalCounter.put(testCon.getLabel(), ++tmp);
	    return !itR.hasNext();
  }
  
	
  // No threshold set
  //TODO: move to Record class if development finished
  public static boolean strictlyAdheresToRDP(Record r, Attribute a) {
	  DataProfile profile = a.getProfile();
	  ProfileStatistic min = profile.getStatistic(StatisticTitle.min);
	  ProfileStatistic max = profile.getStatistic(StatisticTitle.max);

	  Double val = (double) r.getField(a);
	  Double minVal = (double) min.getValue();
	  Double maxVal = (double) max.getValue();
	  if(val >= minVal && val <= maxVal) {
		  return true;
	  }
	  exceptions.add(val);
	  return false;
  }
}
