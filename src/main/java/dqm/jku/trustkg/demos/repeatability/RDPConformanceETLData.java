package dqm.jku.trustkg.demos.repeatability;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.RDPConformanceChecker;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * This class is to evaluate the extent to which the Tributech data loaded in batches of 1,000 records adheres to the RDP boundaries.
 * Threshold: 95 % (0.95)
 * 
 * 
 * @author lisa
 *
 */
public class RDPConformanceETLData {
  private static final int FILEINDEX = 8;
  private static final double THRESHOLD = 0.8;
  private static final int RDP_SIZE = 2000;
  
  private static Map<String, Integer> validityCounter = new HashMap<String, Integer>();
  private static Map<String, Integer> totalCounter = new HashMap<String, Integer>();

  public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
    ConnectorCSV conn = FileSelectionUtil.connectToCSV(FILEINDEX);
    Datasource ds = conn.loadSchema();

    // Initialization of RDPs    
    for (Concept c : ds.getConcepts()) {
      RecordList rs = conn.getPartialRecordList(c, 0, RDP_SIZE);
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
        validityCounter.put(a.getURI(), 0);
        totalCounter.put(a.getURI(), 0);
      }
    }

    // Continuous generation of DPs and conformance checking
    RDPConformanceChecker confChecker = new RDPConformanceChecker(ds, conn, RDP_SIZE, 1000, THRESHOLD);
    confChecker.run();
    // Finally: print evaluation report
    System.out.println(confChecker.getReport());
  }  
}
