package dqm.jku.dqmeerkat.demos.architecture.dbconnections;

import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV1;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.util.concurrent.TimeUnit;

/**
 * Demo class for connection with InfluxDB
 *
 * @author optimusseptim
 * @deprecated Uses outedated dependencies. use {@link InfluxDBNew} instead
 */
@Deprecated
public class InfluxDBDemo {
    public static void main(String args[]) {
        InfluxDBConnectionV1 influxDB = new InfluxDBConnectionV1();

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
