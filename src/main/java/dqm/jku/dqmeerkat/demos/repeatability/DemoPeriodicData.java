package dqm.jku.dqmeerkat.demos.repeatability;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV1;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

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
  private static final int FILEINDEX = 3;
  private static final boolean DEBUG = false;
  private static final boolean DELETE_INFLUX = false;
  private static final int AMOUNT = 100;
  private static final int SLEEP_TIME_MS = 1000;

  public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
    InfluxDBConnectionV1 influx = new InfluxDBConnectionV1();
    int noRecs = 0;
    int offset = 0;
    ConnectorCSV conn = FileSelectionUtil.getConnectorCSV(Constants.FileName.dataCoSupplyChainDataset.getPath());

    if (DELETE_INFLUX) {
      influx.deleteDB();
      influx = new InfluxDBConnectionV1();
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
        // With that, it is also possible to create DataProfiles without changing
        // the reference profile.
        DataProfile dp = a.createDataProfile(rs);
        if (DEBUG) dp.printProfile();
        a.setProfile(dp);
      }
      ds.addProfileToInflux(influx);
      System.out.println(String.format("Profile from batch %d stored!", offset / AMOUNT + 1));
    }

  }

}
