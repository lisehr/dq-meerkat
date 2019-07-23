package dqm.jku.trustkg.demos.alex.visualization;

import java.io.IOException;

import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * Idea: This demo should update the data profile after every recieved record.
 * Since these records from Tributech are all recieved by the time of the implementation,
 * we use a fixed scheduling rate to submit new jobs (i.e. every 20 seconds).
 * 
 * Implementation:
 * - noRecords = 1 in ConnectorPartialCSV
 * - Timers and TimerTasks
 * 
 * @author optimusseptim
 *
 */
public class DemoStreamingData {
  public static void main (String args[]) throws IOException {
    InfluxDBConnection influx = new InfluxDBConnection();
    DSInstanceConnector conn = FileSelectionUtil.connectToCSVPartial(1, 0, 5000);
    
    Datasource ds = conn.loadSchema();
    for (Concept c : ds.getConcepts()) {
      RecordSet rs = conn.getRecordSet(c);
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
      }      
    }
    
    ds.addMeasurementToInflux(influx);
  }
  
  
}
