package dqm.jku.trustkg.demos.alex;

import java.util.concurrent.TimeUnit;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

public class InfluxDBDemo {
  public static void main (String args[]) {
    InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
    String dbName = "aTimeSeries";
    influxDB.query(new Query("CREATE DATABASE " + dbName));
    String rpName = "aRetentionPolicy";
    influxDB.query(new Query("CREATE RETENTION POLICY " + rpName + " ON " + dbName + " DURATION 30h REPLICATION 2 SHARD DURATION 30m DEFAULT"));

    // Flush every 2000 Points, at least every 100ms
    influxDB.enableBatch(BatchOptions.DEFAULTS.actions(2000).flushDuration(100));

    Point point1 = Point.measurement("cpu")
                        .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .addField("idle", 90L)
                        .addField("user", 9L)
                        .addField("system", 1L)
                        .build();
    Point point2 = Point.measurement("disk")
                        .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .addField("used", 80L)
                        .addField("free", 1L)
                        .build();

    influxDB.write(dbName, rpName, point1);
    influxDB.write(dbName, rpName, point2);
    
    Query query = new Query("SELECT idle FROM cpu", dbName);
    printQuery(influxDB.query(query));
    influxDB.query(new Query("DROP RETENTION POLICY " + rpName + " ON " + dbName));
    influxDB.query(new Query("DROP DATABASE " + dbName));
    influxDB.close();
  }
  
  private static void printQuery(QueryResult qr) {
    for (Result r : qr.getResults()) {
      for (Series s : r.getSeries()) {
        System.out.println(s.toString());
      }
    }

  }
}
