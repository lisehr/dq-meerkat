package dqm.jku.dqmeerkat.demos.architecture.dbconnections;

import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV2;
import science.aist.seshat.Logger;

/**
 * <h2>InfluxDBNew</h2>
 * <p>TODO Insert Doc header</p>
 *
 * @author Rainer Meindl, rainer.meindl@scch.at
 * @since 19.01.2022
 **/
public class InfluxDBNew {
    private static final Logger LOGGER = Logger.getInstance();

    public static void main(String[] args) {
        try (var influx = InfluxDBConnectionV2.builder().build()) {
            influx.connect();
            influx.createDatabase("testdb", "testRetention", "1d");
        } catch (Exception e) {
            LOGGER.error(e);
        }


    }
}
