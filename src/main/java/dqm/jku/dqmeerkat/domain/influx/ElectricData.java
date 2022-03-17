package dqm.jku.dqmeerkat.domain.influx;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <h2>ElectroData</h2>
 * <p>POJO representing electric grid data. See {@code resources/data/battery_storage_10000.csv} for details</p>
 *
 * @author Rainer Meindl, rainer.meindl@scch.at
 * @since 26.01.2022
 **/
@Data
@NoArgsConstructor
public class ElectricData {
    private LocalDateTime time;
    /**
     * actually a {@link java.util.UUID}, but not worth parsing now
     */
    private String batteryStorageId;
    private double stateOfCharge;
    private double chargingEnergyWs;
    private String manufacturer;
    private String model;
    private String description;
    private double ratedCapacityWs;

    public ElectricData(List<String> csvLine) {
        time = LocalDateTime.parse(csvLine.get(0), DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss.nnnnnn"));
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
                .time(time.toInstant(ZoneOffset.UTC), WritePrecision.S);
    }
}
