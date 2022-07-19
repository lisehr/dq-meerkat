package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.*;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <h2>DatatypeInfoStatisticsLongTest</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 19.07.2022
 */
@DisplayName("ProfileStatistics: SingleColumn: Datatypeinfo (long)")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class DatatypeInfoStatisticsLongTest {
    private final static long VEHICLE_ID_MINIMUM = 7218891961L;
    private final static long VEHICLE_ID_AVERAGE = 7234653134L;
    private final static long VEHICLE_ID_MAXIMUM = 7240681620L;
    private final static long VEHICLE_ID_MEDIAN = 7235754660L;
    private final static double VEHICLE_ID_STANDARD_DEVIATION = 4602599.10;
    private final static long VEHICLE_ID_MEDIAN_ABSOLUTE_DEVIATION = 0;
    private static DataProfile vehicleIdDP;
    private static DataProfile vehicleRegionDP;
    private static DataProfile vehicleLatitudeDP;
    private static RecordList vehicleRecords;
    private static DataProfile emptyDP;

    @BeforeAll
    @DisplayName("Create a data profiles for the vehicles30000.csv column \"id\", \"lat\", and \"region\"")
    static void setUp() {
        try {
            vehicleIdDP = new DataProfile();
            vehicleRegionDP = new DataProfile();
            vehicleLatitudeDP = new DataProfile();

            DSConnector csvConnector = FileSelectionUtil.getConnectorCSV(Constants.FileName.vehicles.getPath());
            Datasource ds = csvConnector.loadSchema();
            ds.getConcepts().forEach(concept -> {
                Attribute attribute = concept.getAttribute("id");
                vehicleIdDP.setElem(attribute);
                vehicleIdDP.setURI(attribute.getURI() + "/profile");
                attribute = concept.getAttribute("region");
                vehicleRegionDP.setElem(attribute);
                vehicleRegionDP.setURI(attribute.getURI() + "/profile");
                attribute = concept.getAttribute("lat");
                vehicleLatitudeDP.setElem(attribute);
                vehicleLatitudeDP.setURI(attribute.getURI() + "/profile");
                try {
                    vehicleRecords = csvConnector.getRecordList(concept);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            emptyDP = new DataProfile();

            DSConnector csvConnector2 = FileSelectionUtil.getConnectorCSV(Constants.FileName.empty.getPath());
            Datasource ds2 = csvConnector2.loadSchema();
            ds2.getConcepts().forEach(concept -> {
                Attribute attribute = concept.getAttribute("emptycolumn"); // Spaces in names of a column (attribite) get removed...
                emptyDP.setElem(attribute);
                emptyDP.setURI(attribute.getURI() + "/profile");
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


        assertNotNull(vehicleIdDP);
        assertNotNull(vehicleRegionDP);
        assertNotNull(vehicleLatitudeDP);
        assertNotNull(emptyDP);

    }

    @Test
    @DisplayName("Maximum")
    void testMaximumLong() {
        vehicleIdDP.addStatistic(new Maximum(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long maxId = (Long) vehicleIdDP.getStatistic(StatisticTitle.max).getValue();

        assertEquals(VEHICLE_ID_MAXIMUM, maxId);
    }

    @Test
    @DisplayName("Average")
    void testAverageLong() {
        vehicleIdDP.addStatistic(new Average(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long averageId = (Long) vehicleIdDP.getStatistic(StatisticTitle.avg).getValue();

        assertEquals(VEHICLE_ID_AVERAGE, averageId);
    }

    @Test
    @DisplayName("Median")
    void testMedianLong() {
        vehicleIdDP.addStatistic(new Median(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long medianId = (Long) vehicleIdDP.getStatistic(StatisticTitle.med).getValue();

        assertEquals(VEHICLE_ID_MEDIAN, medianId);
    }

    @Test
    @DisplayName("Minimum")
    void testMinimumLong() {
        vehicleIdDP.addStatistic(new Minimum(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long minId = (Long) vehicleIdDP.getStatistic(StatisticTitle.min).getValue();

        assertEquals(VEHICLE_ID_MINIMUM, minId);
    }

    @Test
    @DisplayName("Standard Deviation")
    void testStandardDeviationLong() {
        vehicleIdDP.addStatistic(new DoubleStandardDeviation(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double standardDeviation = (Double) vehicleIdDP.getStatistic(StatisticTitle.sd).getValue(); // The "Long" in the title refernces the type of the attribute which is used for calculating the standard deviation.

        assertEquals(VEHICLE_ID_STANDARD_DEVIATION, standardDeviation, 0.1);
    }

    @Test
    @DisplayName("Median Absolute Deviation")
    void testMedianAbsoluteDeviationLong() {
        vehicleIdDP.addStatistic(new MedianAbsoluteDeviation(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long medianAbsoluteDeviation = (Long) vehicleIdDP.getStatistic(StatisticTitle.mad).getValue(); // The "Long" in the title refernces the type of the attribute which is used for calculating the standard deviation.

        assertEquals(VEHICLE_ID_MEDIAN_ABSOLUTE_DEVIATION, medianAbsoluteDeviation);
    }
}
