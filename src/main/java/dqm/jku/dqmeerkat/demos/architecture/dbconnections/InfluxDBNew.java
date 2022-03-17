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
    /**
     * always the same, as defined in docker compose
     */
    private static final String INFLUX_LOGIN_TOKEN = "12bdc4164c2e8141";
    /**
     * This one sadly keeps changing on container creation
     */
    private static final String INFLUX_ORG_ID = "851b8416fcb11809";

    public static void main(String[] args) {
        try (var influx = InfluxDBConnectionV2.builder()
                .token(INFLUX_LOGIN_TOKEN)
                .build()) {
            influx.connect();
            var token = influx.createDatabase("testdb", 3600);
            LOGGER.info(token);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}