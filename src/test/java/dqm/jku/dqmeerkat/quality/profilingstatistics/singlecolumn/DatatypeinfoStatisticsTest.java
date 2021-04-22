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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class tests profile metrics that are in the package {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo}.
 * The tests are run with the "id", the "region" and/or the "lat" column of the vehicles30000.csv
 * @author Johannes Schrott
 */

@DisplayName("ProfileStatistics: SingleColumn: Datatypeinfo")
class DatatypeinfoStatisticsTest {

    private static DataProfile vehicleIdDP;
    private static DataProfile vehicleRegionDP;
    private static DataProfile vehicleLatitudeDP;
    private static RecordList vehicleRecords;

    // The expected results for the tests where calculated using LibreOffice Calc (Excel has troubles with CSV files)
    static final int NUMBER_OF_RECORDS = 29759;
    static final int NUMBER_OF_NULL_ID = 0; // Number of null values in "id" column
    private final static long VEHICLE_ID_MINIMUM = 7218891961L;
    private final static long VEHICLE_ID_AVERAGE = 7234653134L;
    private final static long VEHICLE_ID_MAXIMUM = 7240681620L;

    private final static double VEHICLE_LATITUDE_MAXIMUM = 64926;
    private final static double VEHICLE_LATITUDE_AVERAGE = 35261.225865;
    private final static double VEHICLE_LATITUDE_MINIMUM = 35261.225865;


    @BeforeAll
    @DisplayName("Create a data profile for the vehicles30000.csv column \"id\"")
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

        } catch (IOException e) {
            e.printStackTrace();
        }


        assertNotNull(vehicleIdDP);
        assertNotNull(vehicleRegionDP);
        assertNotNull(vehicleLatitudeDP);

    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.BasicType} profile statistic.
     * */
    @Test
    @DisplayName("BasicType (no null values)")
    void testBasicType() {
        vehicleIdDP.addStatistic(new BasicType(vehicleIdDP));
        vehicleRegionDP.addStatistic(new BasicType(vehicleRegionDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));
        vehicleRegionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The profilestatistics computed value is a String (this is not the type of the attributes values!)
        assertEquals(String.class, vehicleIdDP.getStatistic(StatisticTitle.bt).getValueClass());
        assertEquals(String.class, vehicleRegionDP.getStatistic(StatisticTitle.bt).getValueClass());

        String countType = (String) vehicleIdDP.getStatistic(StatisticTitle.bt).getValue();
        String nameType = (String) vehicleRegionDP.getStatistic(StatisticTitle.bt).getValue();

        assertEquals("Numeric", countType);
        assertEquals("String", nameType);

    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.DataType} profile statistic.
     * */
    @Test
    @DisplayName("DataType (no null values)")
    void testDataType() {
        vehicleIdDP.addStatistic(new DataType(vehicleIdDP));
        vehicleRegionDP.addStatistic(new DataType(vehicleRegionDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));
        vehicleRegionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The profilestatistics computed value is a String (this is not the type of the attributes values!)
        assertEquals(String.class, vehicleIdDP.getStatistic(StatisticTitle.dt).getValueClass());
        assertEquals(String.class, vehicleRegionDP.getStatistic(StatisticTitle.dt).getValueClass());

        String countType = (String) vehicleIdDP.getStatistic(StatisticTitle.dt).getValue();
        String nameType = (String) vehicleRegionDP.getStatistic(StatisticTitle.dt).getValue();

        assertEquals("Long", countType);
        assertEquals("String", nameType);
    }

    @Test
    @DisplayName("Maximum (long)")
    void testMaximumLong() {
        vehicleIdDP.addStatistic(new Maximum(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long maxId = (Long) vehicleIdDP.getStatistic(StatisticTitle.max).getValue();

        assertEquals(VEHICLE_ID_MAXIMUM, maxId);
    }

    @Test
    @DisplayName("Average (long)")
    void testAverageLong() {
        vehicleIdDP.addStatistic(new Average(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long averageId = (Long) vehicleIdDP.getStatistic(StatisticTitle.avg).getValue();

        assertEquals(VEHICLE_ID_AVERAGE, averageId);
    }

    @Test
    @DisplayName("Minimum (long)")
    void testMinimumLong() {
        vehicleIdDP.addStatistic(new Minimum(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long minId = (Long) vehicleIdDP.getStatistic(StatisticTitle.min).getValue();

        assertEquals(VEHICLE_ID_MINIMUM, minId);
    }

  /*  @Test
    @DisplayName("Maximum (double)")
    void testMaximumDouble() {
        vehicleLatitudeDP.addStatistic(new Maximum(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double maxLat = (Double) vehicleLatitudeDP.getStatistic(StatisticTitle.max).getNumericVal();

        assertEquals(VEHICLE_LATITUDE_MAXIMUM, maxLat);
    }

    @Test
    @DisplayName("Average (double)")
    void testAverageDouble() {
        vehicleLatitudeDP.addStatistic(new Average(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double averageLat = (Double) vehicleLatitudeDP.getStatistic(StatisticTitle.avg).getNumericVal();

        assertEquals(VEHICLE_LATITUDE_AVERAGE, averageLat, 2.0);
    }

    @Test
    @DisplayName("Minimum (double)")
    void testMinimumDouble() {
        vehicleIdDP.addStatistic(new Minimum(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Double minLat = (Long) vehicleIdDP.getStatistic(StatisticTitle.min).getNumericVal();

        assertEquals(VEHICLE_LATITUDE_MINIMUM, minLat);
    }*/

}
