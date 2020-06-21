package dqm.jku.trustkg.demos.alex.kg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.DSDKnowledgeGraph;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * Alternative getting started class for first steps with DQ-MeeRKat
 * 
 * @author optimusseptim
 *
 */
public class GettingStartedAlternative {
  private static final String CSV_PATH = "src/main/java/dqm/jku/trustkg/resources/csv/";

  public static void main(String[] args) throws IOException, NoSuchMethodException {
    File directory = new File(CSV_PATH);
    int fileCount = directory.list().length;

    // Init KG by loading DSD elements
    List<DSConnector> conns = new ArrayList<>();
    for (int i = 1; i <= fileCount; i++) conns.add(FileSelectionUtil.connectToCSV(i));

    // Create Knowledge Graph and add (possibly multiple data sources)
    DSDKnowledgeGraph kg = new DSDKnowledgeGraph("CSVTest");
    kg.addDatasourcesViaConnectors(conns);

    // Annotate reference data profile to KG
    kg.addDataProfile();

    // Persist KG to Embedded GraphDB
    // done automatically

    // Export KG to .ttl file
    // kg.exportKGToFile("CSVTest");

    // Export data profiles to CSV files
    // kg.exportToCSV();

    // Export KG-Report to textfile
    kg.exportReport();
  }
}
