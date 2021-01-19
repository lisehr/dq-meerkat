package dqm.jku.dqmeerkat.demos.architecture.connector;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;

/**
 * Test class for record a CSV file as a whole
 * 
 * @author optimusseptim
 *
 */
public class TestCSVRecording {
  public static void main(String args[]) {
    // Create Connection to CSV Connector
    DSConnector conn = new ConnectorCSV("src/main/java/dqm/jku/dqmeerkat/resources/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);
    try {
      Datasource ds = conn.loadSchema();
      for (Concept c : ds.getConcepts()) {
        Iterator<Record> rIt = conn.getRecords(c);
        while (rIt.hasNext()) {
          Record next = rIt.next();
          System.out.println(next.toString());
        }
      }

    } catch (IOException e) {

    }
  }

}
