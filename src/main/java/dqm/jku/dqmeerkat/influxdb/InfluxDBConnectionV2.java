package dqm.jku.dqmeerkat.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.*;
import dqm.jku.dqmeerkat.util.StringUtil;
import lombok.Builder;
import lombok.Getter;
import org.influxdb.InfluxDB;
import science.aist.seshat.Logger;

import java.util.Arrays;

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
    private final String token;

    private InfluxDBClient influxDB;

    @Getter
    private boolean connected;

    @Builder
    public InfluxDBConnectionV2(String url, String token) {
        if (StringUtil.isNullOrEmpty(url))
            url = "http://localhost:8086";
        this.url = url;

        if (StringUtil.isNullOrEmpty(token))
            token = "root";
        this.token = token;
    }

    /**
     * <p>Connects to the influx db with the given url, user, and password. This method has to be called before this
     * connection is used!</p>
     */
    public void connect() {
        this.influxDB = InfluxDBClientFactory.create(url, token.toCharArray());

        if (!influxDB.ping()) {
            LOGGER.error("Error pinging server.");
            return;
        }
        connected = true;
    }

    public void createDatabaseIfNotExists() {

    }

    public void createDatabase(String databaseName,  int retentionSeconds) {
        var retention = new BucketRetentionRules();
        retention.setEverySeconds(retentionSeconds);
        Bucket bucket = influxDB.getBucketsApi().createBucket(databaseName, retention, token);

        //
        // Create access token to "iot_bucket"
        //
        PermissionResource resource = new PermissionResource();
        resource.setId(bucket.getId());
        resource.setOrgID("12bdc4164c2e8141");
        resource.setType(PermissionResource.TypeEnum.BUCKETS);

        // Read permission
        Permission read = new Permission();
        read.setResource(resource);
        read.setAction(Permission.ActionEnum.READ);

        // Write permission
        Permission write = new Permission();
        write.setResource(resource);
        write.setAction(Permission.ActionEnum.WRITE);

        Authorization authorization = influxDB.getAuthorizationsApi()
                .createAuthorization("12bdc4164c2e8141", Arrays.asList(read, write));

        //
        // Created token that can be use for writes to "iot_bucket"
        //
        String token = authorization.getToken();
        System.out.println("Token: " + token);

    }

    public void write() {
        String data = "mem,host=host1 used_percent=23.43234543";

        WriteApiBlocking writeApi = influxDB.getWriteApiBlocking();
        writeApi.writeRecord("testSeries", "testRetention", WritePrecision.NS, data);

    }


    @Override
    public void close() throws Exception {
        if (!connected)
            return;
        influxDB.close();
        influxDB = null;
    }
}
