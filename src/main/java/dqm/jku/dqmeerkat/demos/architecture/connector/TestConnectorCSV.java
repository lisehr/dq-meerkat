package dqm.jku.dqmeerkat.demos.architecture.connector;

import java.io.IOException;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;

/**
 * Test class for connection with CSV
 * 
 * @author optimusseptim
 *
 */
public class TestConnectorCSV {
  public static void main(String args[]) {
    // Create Connection to CSV Connector
    DSConnector conn = new ConnectorCSV("src/main/java/dqm/jku/trustkg/resources/csv/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);

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
