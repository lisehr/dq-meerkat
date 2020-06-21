package dqm.jku.trustkg.demos.alex.connector;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;

/**
 * Test class for record a CSV file as a whole
 * 
 * @author optimusseptim
 *
 */
public class TestCSVRecording {
  public static void main(String args[]) {
    // Create Connection to CSV Connector
    DSConnector conn = new ConnectorCSV("src/main/java/dqm/jku/trustkg/resources/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);
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
