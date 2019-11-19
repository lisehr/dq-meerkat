package dqm.jku.trustkg.demos.alex.connector;

import java.io.IOException;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;

public class TestConnectorCSV {
  public static void main(String args[]) {
    // Create Connection to CSV Connector
    DSInstanceConnector conn = new ConnectorCSV("src/main/java/dqm/jku/trustkg/resources/csv/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);

    // Create Schema from it
    Datasource ds;
    try {
      ds = conn.loadSchema();
      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());

        for (Attribute a : c.getAttributes()) {
          System.out.println(a.getDataType().toString() + "\t" + a.getURI());
        }
        System.out.println();
      }

    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }

  }
}
