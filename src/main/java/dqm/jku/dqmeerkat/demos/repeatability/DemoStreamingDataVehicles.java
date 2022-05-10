package dqm.jku.dqmeerkat.demos.repeatability;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV1;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Idea: This demo should update the data profile after every received record.
 * Since these records from the vehicles30000 csv file are all received by the time of the
 * implementation, we use a scheduling algorithm {@see createTimeout} to submit new jobs.
 * 
 * 
 * @author Rebecca Rachinger (based on the DemoStreamingData by optimusseptim)
 *
 */
public class DemoStreamingDataVehicles {
  private static final boolean DEBUG = true;

  public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
    InfluxDBConnectionV1 influx = new InfluxDBConnectionV1();

    ConnectorCSV conn = FileSelectionUtil.getConnectorCSV(Constants.FileName.vehicles.getPath());

    if (DEBUG) {
      influx.deleteDB();
      influx = new InfluxDBConnectionV1();
    }

    Concept testCon = null;

    Datasource ds = conn.loadSchema();


    for (Concept c : ds.getConcepts()) {
      RecordList rs = conn.getPartialRecordList(c,  0, 5000);
      int i =0;
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
        i = i+1;
      }
      testCon = c;
    }





    ds.addProfileToInflux(influx);
    RecordList rs = conn.getPartialRecordList(testCon, 5001, Integer.MAX_VALUE);
    boolean fileFinished = false;
    int i = 0;
    Iterator<Record> itR = rs.iterator();
    while (!fileFinished) {
      fileFinished = addMeasurement(influx, testCon, itR);
      // adds one measurement to the profile (static, but periodic), so limits can be
      // displayed
      ds.addProfileToInflux(influx);
      System.out.println("Record " + (i + 1) + " stored!");
      createTimeout(i);
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
  private static boolean addMeasurement(InfluxDBConnectionV1 influx, Concept testCon, Iterator<Record> itR) throws IOException {
    if (!itR.hasNext()) return true;
    influx.storeRecord(itR.next());
    return !itR.hasNext();
  }

}
