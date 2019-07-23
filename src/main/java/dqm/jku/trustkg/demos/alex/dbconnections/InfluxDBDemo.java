package dqm.jku.trustkg.demos.alex.dbconnections;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import dqm.jku.trustkg.influxdb.InfluxDBConnection;

public class InfluxDBDemo {
  public static void main(String args[]) {
    InfluxDBConnection influxDB = new InfluxDBConnection();

    Point point1 = Point.measurement("cpu").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("idle", 90L).addField("user", 9L).addField("system", 1L).build();
    Point point2 = Point.measurement("disk").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("used", 80L).addField("free", 1L).build();

    influxDB.write(point1);
    influxDB.write(point2);

    Query query = new Query("SELECT idle FROM cpu", influxDB.getDbName());
    influxDB.printQuery(influxDB.query(query));
    influxDB.deleteDB();
    influxDB.close();
  }
}
