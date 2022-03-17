package dqm.jku.dqmeerkat.demos.architecture.dbconnections;

import dqm.jku.dqmeerkat.domain.influx.ElectricData;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV2;
import science.aist.seshat.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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
        List<ElectricData> electricData;
        try {
            electricData = Files.readAllLines(Path.of("src/main/resource/data/battery_storage_10000.csv")).stream()
                    .skip(1) // skip csvheader
                    .map(s -> s.split(","))
                    .map(ElectricData::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("Could not read file");
            return;
        }



        try (var influx = InfluxDBConnectionV2.builder()
                .token(INFLUX_LOGIN_TOKEN)
                .orgId(INFLUX_ORG_ID)
                .build()) {
            influx.connect();
            var token = influx.createDatabase("testdb", 3600);
            LOGGER.info("token: " + token);
            influx.write(electricData);

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}