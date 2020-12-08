package dqm.jku.trustkg.demos.repeatability;

import java.io.IOException;

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
public class RDPConformanceTributechData {
  private static final int FILEINDEX = 8;
  private static final double THRESHOLD = 0.1;		// Threshold indicates allowed deviation from reference value in percent
  private static final int RDP_SIZE = 1000;
  private static final int BATCH_SIZE = 1000;		// Set to 1 to simulate streaming data

  public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
    ConnectorCSV conn = FileSelectionUtil.connectToCSV(FILEINDEX);
    Datasource ds = conn.loadSchema();

    // Initialization of RDPs    
    for (Concept c : ds.getConcepts()) {
      RecordList rs = conn.getPartialRecordList(c, 0, RDP_SIZE);
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
      }
    }

    // Continuous generation of DPs and conformance checking
    RDPConformanceChecker confChecker = new RDPConformanceChecker(ds, conn, RDP_SIZE, BATCH_SIZE, THRESHOLD);
    confChecker.run();
    // Finally: print evaluation report
    System.out.println(confChecker.getReport());
  }
}
