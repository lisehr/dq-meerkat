package dqm.jku.dqmeerkat.demos.architecture.kg;

import dqm.jku.dqmeerkat.connectors.ConnectorMongoDBFlatten;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GettingStartedAlternativeMongoDB {
  private static final String CSV_PATH = "src/main/java/dqm/jku/dqmeerkat/resources/csv/";

  public static void main(String[] args) throws IOException, NoSuchMethodException {
    // Init KG by loading DSD elements
    List<DSConnector> conns = new ArrayList<>();
    DSConnector connector = new ConnectorMongoDBFlatten("localhost", "sample_restaurants", "admin", "adminpw");
    conns.add(connector);
    // Create Knowledge Graph and add (possibly multiple data sources)
    DSDKnowledgeGraph kg = new DSDKnowledgeGraph("MongoDBTest");
    kg.addDatasourcesViaConnectors(conns);

    // Annotate reference data profile to KG
    kg.addDataProfile();

    // Persist KG to Embedded GraphDB
    // done automatically

    // Export KG to .ttl file
    // kg.exportKGToFile("MongoDB Test");

    // Export data profiles to CSV files
    // kg.exportToCSV();

    // Export KG-Report to textfile
    //kg.exportReport();

    // Print KG-Report
    kg.printAll();
  }
}
