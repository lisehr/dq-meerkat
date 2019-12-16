package dqm.jku.trustkg.demos.repeatability;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * Idea: This demo should update the data profile after every received record.
 * Since these records from Tributech are all received by the time of the
 * implementation, we use a fixed scheduling rate to submit new jobs (i.e. every
 * 20 seconds).
 * 
 * 
 * @author optimusseptim
 *
 */
public class DemoStreamingData {
  private static final int FILEINDEX = 14;
  private static final boolean DEBUG = false;

  public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
    InfluxDBConnection influx = new InfluxDBConnection();
    ConnectorCSV conn = FileSelectionUtil.connectToCSV(FILEINDEX);

    if (DEBUG) {
      influx.deleteDB();
      influx = new InfluxDBConnection();
    }

    Concept testCon = null;

    Datasource ds = conn.loadSchema();
    for (Concept c : ds.getConcepts()) {
      RecordList rs = conn.getPartialRecordList(c, 0, 5000);
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
      }
      testCon = c;
    }

    ds.addProfileToInflux(influx);
    RecordList rs = conn.getPartialRecordList(testCon, 5001, Integer.MAX_VALUE);
    boolean fileFinished = false;
    int i = 0;
    Iterator<Record> itR = rs.iterator();
    while (!fileFinished) {
      createTimeout(i);
      fileFinished = addMeasurement(influx, testCon, itR);
      // adds one measurement to the profile (static, but periodic), so limits can be
      // displayed
      ds.addProfileToInflux(influx);
      System.out.println("Record " + i + " stored!");
      i++;
    }

  }

  /**
   * Creates a timeout based on random numbers. If the random number between 1 and
   * 50 is less or equal to the current index modulo 50, then index modulo 50
   * seconds are waited, otherwise one second is waited.
   * 
   * @param i the index used for calculation
   * @throws InterruptedException
   */
  private static void createTimeout(int i) throws InterruptedException {
    int rand = (int) (Math.random() * 15 + 1);
    if (rand <= i % 15) TimeUnit.SECONDS.sleep(i % 15);
    else TimeUnit.MILLISECONDS.sleep(500);
  }

  /**
   * Adds one measurement record to influxDB.
   * 
   * @param influx  the influxDB connection
   * @param testCon the Concept to be tested
   * @param itR     iterator for records
   * @return true if the file is finished, false if not
   * @throws IOException
   */
  private static boolean addMeasurement(InfluxDBConnection influx, Concept testCon, Iterator<Record> itR) throws IOException {
    if (!itR.hasNext()) return true;
    influx.storeRecord(itR.next());
    return !itR.hasNext();
  }

}
