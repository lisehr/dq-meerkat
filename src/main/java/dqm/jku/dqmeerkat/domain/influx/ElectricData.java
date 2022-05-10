package dqm.jku.dqmeerkat.domain.influx;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <h2>ElectroData</h2>
 * <p>POJO representing electric grid data. See {@code resources/data/battery_storage_single.csv} for details</p>
 *
 * @author Rainer Meindl, rainer.meindl@scch.at
 * @since 26.01.2022
 **/
@Data
@NoArgsConstructor
@Measurement(name = "ElectricData")
public class ElectricData {
    @Column(timestamp = true)
    private Instant time;
    /**
     * actually a {@link java.util.UUID}, but not worth parsing now
     */
    @Column(tag = true)
    private String batteryStorageId;
    @Column
    private double stateOfCharge;
    @Column
    private double chargingEnergyWs;
    @Column(tag = true)
    private String manufacturer;
    @Column(tag = true)
    private String model;
    @Column(tag = true)
    private String description;
    @Column
    private double ratedCapacityWs;

    public ElectricData(List<String> csvLine) {
        time = LocalDateTime.parse(csvLine.get(0), DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss.nnnnnn"))
                .toInstant(ZoneOffset.UTC);
        batteryStorageId = csvLine.get(1);
        stateOfCharge = Double.parseDouble(csvLine.get(2));
        chargingEnergyWs = Double.parseDouble(csvLine.get(3));
        manufacturer = csvLine.get(4);
        model = csvLine.get(5);
        description = csvLine.get(6);
        ratedCapacityWs = Double.parseDouble(csvLine.get(7));
    }

    public ElectricData(String[] strings) {
        this(List.of(strings));
    }


    public Point toInfluxPoint() {
        return Point.measurement("ElectricData")
                .addField("stateOfCharge", stateOfCharge)
                .addField("chargingEnergyWs", chargingEnergyWs)
                .addField("ratedCapacity", ratedCapacityWs)
                .time(time, WritePrecision.S);
    }
}
