package dqm.jku.trustkg.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.quality.DataProfile;

public class InfluxDBConnection {
  private static final String URL = "http://localhost:8086";
  private static final String USER = "root";
  private static final String PW = "root";
  private static final String DEF_DB = "testSeries";
  private static final String DEF_RET = "testRetention";

  private InfluxDB instance;
  private String dbName;
  private String retentionPolicyName;

  public InfluxDBConnection() {
    instance = InfluxDBFactory.connect(URL, USER, PW);
    this.dbName = DEF_DB;
    this.retentionPolicyName = DEF_RET;
    createDB();
  }
  
  public InfluxDBConnection(String dbName) {
    instance = InfluxDBFactory.connect(URL, USER, PW);
    this.dbName = dbName;
    this.retentionPolicyName = DEF_RET;
    createDB();
  }
  
  public InfluxDBConnection(String dbName, String retentionPolicyName) {
    instance = InfluxDBFactory.connect(URL, USER, PW);
    this.dbName = dbName;
    this.retentionPolicyName = retentionPolicyName;
    createDB();
  }


  /**
   * Gets the db name
   * 
   * @return the dbName
   */
  public String getDbName() {
    return dbName;
  }

  /**
   * Gets the name of the retention policy
   * 
   * @return the retentionPolicyName
   */
  public String getRetentionPolicyName() {
    return retentionPolicyName;
  }

  /**
   * Helper method for creating the database
   */
  private void createDB() {
    if (isInitialized()) return;
    instance.query(new Query("CREATE DATABASE " + dbName));
    instance.query(new Query("CREATE RETENTION POLICY " + retentionPolicyName + " ON " + dbName + " DURATION 30h REPLICATION 2 SHARD DURATION 30m DEFAULT"));
  }

  /**
   * Check if the database is initialized
   * 
   * @return true if so, false otherwise
   */
  public boolean isInitialized() {
    QueryResult dbs = instance.query(new Query("SHOW DATABASES"));
    for (Result r : dbs.getResults()) {
      for (Series s : r.getSeries()) {
        if(s.getValues().stream().flatMap(x -> x.stream()).anyMatch(str -> str.equals(dbName))) return true;
      }
    }
    return false;
  }

  /**
   * Writes a measuring point into the db
   * 
   * @param value the point to be written
   */
  public void write(Point value) {
    this.instance.write(dbName, retentionPolicyName, value);
  }

  /**
   * Executes a query to the database
   * 
   * @param query the query to be executed
   * @return query result of the query
   */
  public QueryResult query(Query query) {
    return this.instance.query(query);
  }

  /**
   * Method for closing the database instance
   */
  public void close() {
    this.instance.close();
  }

  /**
   * Method for deleting the database
   */
  public void deleteDB() {
    instance.query(new Query("DROP RETENTION POLICY " + getRetentionPolicyName() + " ON " + getDbName()));
    instance.query(new Query("DROP DATABASE " + getDbName()));
  }

  /**
   * Method for printing a query result
   * 
   * @param qr the query result to be printed
   */
  public void printQuery(QueryResult qr) {
    for (Result r : qr.getResults()) {
      if (r.getSeries() == null) return;
      for (Series s : r.getSeries()) {
        System.out.println(s.toString());
      }
    }
  }
  
  /**
   * Stores the content of one record into influxDB. Stores nothing if null
   * @param record the record to be stored
   */
  public void storeRecord(Record record) {
    if (record == null) return;
    write(record.assignedFrom.createMeasurement(record));
  }
  
  public void storeProfile(DataProfile profile) {
    if (profile == null || profile.getElem() == null) return;
    profile.getElem().storeProfile(this, profile);
  }

}
