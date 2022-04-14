package dqm.jku.dqmeerkat.influxdb;

import org.influxdb.dto.Point;

/**
 * <h2>InfluxDBConnection</h2>
 * <p>TODO Insert Doc header</p>
 *
 * @author Rainer Meindl, rainer.meindl@scch.at
 * @since 19.01.2022
 **/
public interface InfluxDBConnection extends AutoCloseable{
    void write(Point measuringPoint);
}
