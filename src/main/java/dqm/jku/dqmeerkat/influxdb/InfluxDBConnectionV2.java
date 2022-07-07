package dqm.jku.dqmeerkat.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.*;
import dqm.jku.dqmeerkat.util.StringUtil;
import lombok.Builder;
import lombok.Getter;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.jetbrains.annotations.NotNull;
import science.aist.seshat.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * <h2>InfluxDBConnectionV2</h2>
 * <p>Wraps a {@link InfluxDB} connection and allows easy handling of it.</p>
 *
 * @author Rainer Meindl, rainer.meindl@scch.at
 * @since 19.01.2022
 **/
public class InfluxDBConnectionV2 implements InfluxDBConnection {
    private static final Logger LOGGER = Logger.getInstance();

    private final String url;
    private final String token;
    private final String orgId;
    private InfluxDBClient influxDB;

    @Getter
    private boolean connected;

    @Builder
    public InfluxDBConnectionV2(String url, String token, String orgId) {
        if (StringUtil.isNullOrEmpty(url))
            url = "http://localhost:8086";
        this.url = url;
        this.token = token;
        this.orgId = orgId;
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


    /**
     * <p>
     * Similar to createDatabaseIfNotExist, this method creates a database/bucket and provides an access token for
     * read/write access. If a database with the given name already exists the access token is retrieved and returned.
     * If for some reason no token is found a new one is created.
     * </p>
     *
     * @param databaseName     the unique name for the bucket.
     * @param retentionSeconds How long data is retained in the bucket (?)
     * @return Read/Write Access token to the created bucket
     */
    public String createDatabase(String databaseName, int retentionSeconds) {
        var bucket = influxDB.getBucketsApi().findBucketByName(databaseName);
        if (bucket == null)
            return createDatabaseIfNotExists(databaseName, retentionSeconds);
        return influxDB.getAuthorizationsApi().findAuthorizationsByOrgID(orgId).stream()
                .filter(authorization -> authorization.getDescription() != null &&
                        !authorization.getDescription().equalsIgnoreCase("admin's Token"))
                .findFirst()
                .orElseGet(() -> createAuthorisationForBucket(bucket)).getToken();
    }

    public void deleteDatabase(String name) {
        var bucket = influxDB.getBucketsApi().findBucketByName(name);
        if (bucket != null) {
            influxDB.getBucketsApi().deleteBucket(bucket.getId());
        }
    }

    /**
     * <p>Creates a new databse/bucket in influxdb. It is part of the organisation defined in this connection.
     * In order to use the given database, use the r/w token, that is returned by this method. This method throws
     * an exception if the bucket already exists.</p>
     *
     * @param databaseName     the unique name for the bucket.
     * @param retentionSeconds How long data is retained in the bucket (?)
     * @return Read/Write Access token to the created bucket
     */
    public String createDatabaseIfNotExists(String databaseName, int retentionSeconds) {
        var retention = new BucketRetentionRules();
        retention.setEverySeconds(retentionSeconds);
        Bucket bucket = influxDB.getBucketsApi().createBucket(databaseName, retention, orgId);

        // Create access token to bucket
        Authorization authorization = createAuthorisationForBucket(bucket);

        return authorization.getToken();

    }

    @NotNull
    private Authorization createAuthorisationForBucket(Bucket bucket) {
        PermissionResource resource = new PermissionResource();
        resource.setId(bucket.getId());
        resource.setOrgID(orgId);
        resource.setType(PermissionResource.TypeEnum.BUCKETS);

        // Read permission
        Permission read = new Permission();
        read.setResource(resource);
        read.setAction(Permission.ActionEnum.READ);

        // Write permission
        Permission write = new Permission();
        write.setResource(resource);
        write.setAction(Permission.ActionEnum.WRITE);

        return influxDB.getAuthorizationsApi()
                .createAuthorization(orgId, Arrays.asList(read, write));
    }

    public <T> void write(String bucketname, T pojo) {
        WriteApiBlocking writeApi = influxDB.getWriteApiBlocking();

        writeApi.writeMeasurement(bucketname, orgId, WritePrecision.S, pojo);

    }

    public <T> void write(String bucketname, List<T> pojo) {
        WriteApiBlocking writeApi = influxDB.getWriteApiBlocking();
        writeApi.writeMeasurements(bucketname, orgId, WritePrecision.S, pojo);
    }


    public <T> List<T> read(String query, Class<T> pojoClass) {
        var readApi = influxDB.getQueryApi();
        return readApi.query(query, orgId, pojoClass);
    }

    @Override
    public void close() throws Exception {
        if (!connected)
            return;
        influxDB.close();
        influxDB = null;
    }

    @Override
    public void write(String bucketName, Point measurement) {
        throw new UnsupportedOperationException(
                String.format("Cannot use wrong Point class {%s} in implementation {%s}",
                        Point.class, this.getClass()));
    }

    @Override
    public void write(Point measuringPoint) {
        throw new UnsupportedOperationException(
                String.format("Cannot use wrong Point class {%s} in implementation {%s}",
                        Point.class, this.getClass()));
    }

    @Override
    public void write(String bucketName, com.influxdb.client.write.Point measurement) {
        WriteApiBlocking writeApi = influxDB.getWriteApiBlocking();
        writeApi.writePoint(bucketName, orgId, measurement);
    }

    @Override
    public void write(com.influxdb.client.write.Point measuringPoint) {
        write("default", measuringPoint);
    }
}
