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
 * This class tests profile statistics that are in the package {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo}.
 * The tests are run with the "id", the "region" and/or the "lat" column of the vehicles30000.csv and the empty.csv
 *
 * @author Johannes Schrott
 */

@DisplayName("ProfileStatistics: SingleColumn: Datatypeinfo (double)")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class DatatypeInfoStatisticsDoubleTest {
    private final static int VEHICLE_ID_DIGITS = 10;
    private final static int VEHICLE_ID_DECIMALS = 0;
    private final static long VEHICLE_ID_MINIMUM = 7218891961L;
    private final static long VEHICLE_ID_AVERAGE = 7234653134L;
    private final static long VEHICLE_ID_MAXIMUM = 7240681620L;
    private final static long VEHICLE_ID_MEDIAN = 7235754660L;
    private final static double VEHICLE_ID_STANDARD_DEVIATION = 4602599.10;
    private final static long VEHICLE_ID_MEDIAN_ABSOLUTE_DEVIATION = 0;
    private final static int VEHICLE_LATITUDE_DIGITS = 2;
    private final static int VEHICLE_LATITUDE_DECIMALS = 15; // visible in csv text file; excel truncates decimals to 6
    private final static double VEHICLE_LATITUDE_MAXIMUM = 64.993698;
    private final static double VEHICLE_LATITUDE_AVERAGE = 38.09251058;
    private final static double VEHICLE_LATITUDE_MINIMUM = -79.80964;
    private final static double VEHICLE_LATITUDE_MEDIAN = 34.668793;
    private final static double VEHICLE_LATITUDE_STANDARD_DEVIATION = 9.07;
    private final static double VEHICLE_LATITUDE_MEDIAN_ABSOLUTE_DEVIATION = 0.022574;
    private final static int VEHICLE_REGION_DIGITS = 0;
    private final static int VEHICLE_REGION_DECIMALS = 0;
    private static DataProfile vehicleIdDP;
    private static DataProfile vehicleRegionDP;
    private static DataProfile vehicleLatitudeDP;
    private static RecordList vehicleRecords;
    private static DataProfile emptyDP;
    private static RecordList emptyRecords;

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
                try {
                    emptyRecords = csvConnector2.getRecordList(concept);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    @DisplayName("Maximum (double)")
    void testMaximumDouble() {
        vehicleLatitudeDP.addStatistic(new Maximum(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double maxLat = (Double) vehicleLatitudeDP.getStatistic(StatisticTitle.max).getValue();

        assertEquals(VEHICLE_LATITUDE_MAXIMUM, maxLat);
    }

    @Test
    @DisplayName("Average (double)")
    void testAverageDouble() {
        vehicleLatitudeDP.addStatistic(new Average(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double averageLat = (Double) vehicleLatitudeDP.getStatistic(StatisticTitle.avg).getValue();

        assertEquals(VEHICLE_LATITUDE_AVERAGE, averageLat, 2.0);
    }

    @Test
    @DisplayName("Minimum (double)")
    void testMinimumDouble() {
        vehicleLatitudeDP.addStatistic(new Minimum(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double minLat = (Double) vehicleLatitudeDP.getStatistic(StatisticTitle.min).getValue();

        assertEquals(VEHICLE_LATITUDE_MINIMUM, minLat);
    }

    @Test
    @DisplayName("Median (double)")
    void testMedianDouble() {
        vehicleLatitudeDP.addStatistic(new Median(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double medianLat = (Double) vehicleLatitudeDP.getStatistic(StatisticTitle.med).getValue();

        assertEquals(VEHICLE_LATITUDE_MEDIAN, medianLat, 2.0);
    }

    @Test
    @DisplayName("Standard Deviation (double)")
    void testStandardDeviationDouble() {
        vehicleLatitudeDP.addStatistic(new StandardDeviation(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double standardDeviation = (Double) vehicleLatitudeDP.getStatistic(StatisticTitle.sd).getValue();

        assertEquals(VEHICLE_LATITUDE_STANDARD_DEVIATION, standardDeviation, 0.1);
    }

    @Test
    @DisplayName("Median Abslolute Deviation (double)")
    void testMedianAbsoluteDeviationDouble() {
        vehicleLatitudeDP.addStatistic(new MedianAbsoluteDeviation(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));
        //vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculationNumeric());
        Double medianAbsoluteDeviation = (Double) vehicleLatitudeDP.getStatistic(StatisticTitle.mad).getValue();

        assertEquals(VEHICLE_LATITUDE_MEDIAN_ABSOLUTE_DEVIATION, medianAbsoluteDeviation, 0.1);
    }

}
