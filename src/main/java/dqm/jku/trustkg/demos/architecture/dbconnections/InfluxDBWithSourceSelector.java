package dqm.jku.trustkg.demos.architecture.dbconnections;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * Test class for InfluxDB with CSV Source selector
 * 
 * @author optimusseptim
 *
 */
public class InfluxDBWithSourceSelector {
  private static final boolean DEBUG = false;

  public static void main(String args[]) throws IOException {
    InfluxDBConnection influxDB = new InfluxDBConnection();
    DSConnector conn = FileSelectionUtil.connectToCSV(1);

    Datasource ds;
    try {
      ds = conn.loadSchema();
      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());

        for (Attribute a : c.getAttributes()) {
          System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
        }
        System.out.println();
      }

      if (DEBUG) {
        for (Concept c : ds.getConcepts()) {
          Iterator<Record> rIt = conn.getRecords(c);
          while (rIt.hasNext()) {
            Record next = rIt.next();
            System.out.println(next.toString());
          }
          System.out.println();
        }
      }

      System.out.println("Changes on the Scheme:");

      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());
        RecordList rs = conn.getRecordList(c);
        for (Attribute a : c.getAttributes()) {
          a.annotateProfile(rs);

          System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
          a.printAnnotatedProfile();
        }
        System.out.println();
      }

      ds.addProfileToInflux(influxDB);
      if (DEBUG) influxDB.deleteDB();
      influxDB.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
