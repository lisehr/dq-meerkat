package dqm.jku.trustkg.demos.repeatability;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * Idea: This demo should update the data profile after any fixed amount of
 * records (let's say after every set of 1000 records in a 100k record set) to
 * show how the data profile has changed over time
 * 
 * Implementation: - batch updates via partial csv connector - update methods in
 * data profile
 * 
 * @author optimusseptim
 *
 */
public class DemoPeriodicData {
  private static final int FILEINDEX = 2;
  private static final boolean DEBUG = false;
  private static final boolean DELETE_INFLUX = false;
  private static final int AMOUNT = 100;
  private static final int SLEEP_TIME_MS = 1000;
  private static final String NAME = "supplychain";

  public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
    InfluxDBConnection influx = new InfluxDBConnection();
    int noRecs = 0;
    int offset = 0;
    ConnectorCSV conn = FileSelectionUtil.connectToCSV(FILEINDEX, NAME);

    if (DELETE_INFLUX) {
      influx.deleteDB();
      influx = new InfluxDBConnection();
    }

    Concept testCon = null;

    Datasource ds = conn.loadSchema();
    for (Concept c : ds.getConcepts()) {
      testCon = c;
      RecordList rs = conn.getPartialRecordList(c, 0, AMOUNT);
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
        if (DEBUG) a.printAnnotatedProfile();
      }
    }
    noRecs = conn.getNrRecords(testCon);

    ds.addProfileToInflux(influx);
    System.out.println("Profile from batch 1 stored!");

    for (offset = AMOUNT + 1; offset < noRecs; offset += AMOUNT) {
      TimeUnit.MILLISECONDS.sleep(SLEEP_TIME_MS);
      RecordList rs = conn.getPartialRecordList(testCon, offset, AMOUNT);
      for (Attribute a : testCon.getSortedAttributes()) {
        DataProfile dp = a.createDataProfile(rs);
        if (DEBUG) dp.printProfile();
        influx.storeProfile(dp);
      }
      System.out.println(String.format("Profile from batch %d stored!", offset/AMOUNT + 1));
    }

  }

}
