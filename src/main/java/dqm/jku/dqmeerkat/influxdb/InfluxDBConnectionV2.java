package dqm.jku.dqmeerkat.influxdb;

import dqm.jku.dqmeerkat.util.StringUtil;
import lombok.Builder;
import lombok.Getter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import science.aist.seshat.Logger;

/**
 * <h2>InfluxDBConnectionV2</h2>
 * <p>Wraps a {@link InfluxDB} connection and allows easy handling of it.</p>
 *
 * @author Rainer Meindl, rainer.meindl@scch.at
 * @since 19.01.2022
 **/
public class InfluxDBConnectionV2 implements AutoCloseable {
    private static final Logger LOGGER = Logger.getInstance();

    private final String url;
    private final String username;
    private final String password;

    private InfluxDB influxDB;

    @Getter
    private boolean connected;

    @Builder
    public InfluxDBConnectionV2(String url, String username, String password) {
        if (StringUtil.isNullOrEmpty(url))
            url = "http://localhost:8086";
        this.url = url;
        if (StringUtil.isNullOrEmpty(username))
            username = "root";
        this.username = username;
        if (StringUtil.isNullOrEmpty(password))
            password = "root";
        this.password = password;
    }

    /**
     * <p>Connects to the influx db with the given url, user, and password. This method has to be called before this
     * connection is used!</p>
     */
    public void connect() {
        this.influxDB = InfluxDBFactory.connect(url, username, password);
        Pong response = influxDB.ping();
        if (response.getVersion().equalsIgnoreCase("unknown")) {
            LOGGER.error("Error pinging server.");
            return;
        }
        connected = true;
    }

    public void createDatabaseIfNotExists() {

    }

    public void createDatabase(String databaseName, String retentionPolicy, String expirationTime) {
        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);
        influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicy
                + " ON " + databaseName + " DURATION " + expirationTime + "  REPLICATION 1 DEFAULT"));
        influxDB.setRetentionPolicy(retentionPolicy);

    }


    @Override
    public void close() throws Exception {
        if (!connected)
            return;
        influxDB.close();
        influxDB = null;
    }
}
