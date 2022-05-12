package dqm.jku.dqmeerkat.influxdb;

import org.influxdb.dto.Point;

/**
 * <h2>InfluxDBConnection</h2>
 * <p>Interface for providing version independent access to a influxdb. Currently write only, further logic is
 * to be done</p>
 *
 * @author Rainer Meindl, rainer.meindl@scch.at
 * @since 19.01.2022
 **/
public interface InfluxDBConnection extends AutoCloseable {
    void write(String bucketName, Point measurement);

    void write(Point measuringPoint);

    void write(String bucketName, com.influxdb.client.write.Point measurement);

    void write(com.influxdb.client.write.Point measuringPoint);
}
