package dqm.jku.trustkg.demos.alex;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;

public class TestCSVRecording {
  public static void main(String args[]) {
 // Create Connection to CSV Connector
    DSInstanceConnector conn = new ConnectorCSV(
        "src/main/java/dqm/jku/trustkg/resources/Telematic Device Report - Device Voltage.csv", ",", "\n",
        "Device Voltage", true);
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
