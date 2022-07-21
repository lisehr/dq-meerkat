package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.BasicType;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.DataType;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.Decimals;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.Digits;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <h2>DatatypeInfoStatisticsTest</h2>
 * <summary>ProfileStatistic test class for generic statistics</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 19.07.2022
 */
@DisplayName("ProfileStatistics: SingleColumn: Datatypeinfo")
@TestMethodOrder(MethodOrderer.DisplayName.class)

public class DatatypeInfoStatisticsTest {
    private final static int VEHICLE_ID_DIGITS = 10;
    private final static int VEHICLE_ID_DECIMALS = 0;
    private final static int VEHICLE_LATITUDE_DIGITS = 2;
    private final static int VEHICLE_LATITUDE_DECIMALS = 15; // visible in csv text file; excel truncates decimals to 6
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

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.BasicType} profile statistic.
     */
    @Test
    @DisplayName("BasicType")
    void testBasicType() {
        vehicleIdDP.addStatistic(new BasicType(vehicleIdDP));
        vehicleRegionDP.addStatistic(new BasicType(vehicleRegionDP));

        emptyDP.addStatistic(new BasicType(emptyDP));


        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));
        vehicleRegionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        emptyDP.getStatistics().forEach(statistic -> statistic.calculation(emptyRecords, null));

        // The profilestatistics computed value is a String (this is not the type of the attributes values!)
        assertEquals(String.class, vehicleIdDP.getStatistic(StatisticTitle.bt).getInputValueClass());
        assertEquals(String.class, vehicleRegionDP.getStatistic(StatisticTitle.bt).getInputValueClass());

        assertEquals(String.class, emptyDP.getStatistic(StatisticTitle.bt).getInputValueClass());


        String countType = (String) vehicleIdDP.getStatistic(StatisticTitle.bt).getValue();
        String nameType = (String) vehicleRegionDP.getStatistic(StatisticTitle.bt).getValue();
        String emptyType = (String) emptyDP.getStatistic(StatisticTitle.bt).getValue();

        assertEquals("Numeric", countType);
        assertEquals("String", nameType);
        assertEquals("Object", emptyType); // Object is returned for null (--> the type is undefined)

    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.DataType} profile statistic.
     */
    @Test
    @DisplayName("DataType (no null values)")
    void testDataType() {
        vehicleIdDP.addStatistic(new DataType(vehicleIdDP));
        vehicleRegionDP.addStatistic(new DataType(vehicleRegionDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));
        vehicleRegionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The profilestatistics computed value is a String (this is not the type of the attributes values!)
        assertEquals(String.class, vehicleIdDP.getStatistic(StatisticTitle.dt).getInputValueClass());
        assertEquals(String.class, vehicleRegionDP.getStatistic(StatisticTitle.dt).getInputValueClass());

        String countType = (String) vehicleIdDP.getStatistic(StatisticTitle.dt).getValue();
        String nameType = (String) vehicleRegionDP.getStatistic(StatisticTitle.dt).getValue();

        assertEquals("Long", countType);
        assertEquals("String", nameType);
    }

    @Test
    @DisplayName("Digits")
    void testDigits() {

        vehicleIdDP.addStatistic(new Digits(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        vehicleLatitudeDP.addStatistic(new Digits(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        vehicleRegionDP.addStatistic(new Digits(vehicleRegionDP));
        vehicleRegionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));


        var idDigits = (Long) vehicleIdDP.getStatistic(StatisticTitle.dig).getValue();
        var latitudeDigits = (Long) vehicleLatitudeDP.getStatistic(StatisticTitle.dig).getValue();
        var regionDigits = (Long) vehicleRegionDP.getStatistic(StatisticTitle.dig).getValue();


        assertEquals(VEHICLE_ID_DIGITS, idDigits);
        assertEquals(VEHICLE_LATITUDE_DIGITS, latitudeDigits);
        assertEquals(VEHICLE_REGION_DIGITS, regionDigits);

    }

    @Test
    @DisplayName("Decimals")
    void testDecimals() {

        vehicleIdDP.addStatistic(new Decimals(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        vehicleLatitudeDP.addStatistic(new Decimals(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        vehicleRegionDP.addStatistic(new Decimals(vehicleRegionDP));
        vehicleRegionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));


        Integer idDecimals = (Integer) vehicleIdDP.getStatistic(StatisticTitle.dec).getValue();
        Integer latitudeDecimals = (Integer) vehicleLatitudeDP.getStatistic(StatisticTitle.dec).getValue();
        Integer regionDecimals = (Integer) vehicleRegionDP.getStatistic(StatisticTitle.dec).getValue();


        assertEquals(VEHICLE_ID_DECIMALS, idDecimals);
        assertEquals(VEHICLE_LATITUDE_DECIMALS, latitudeDecimals);
        assertEquals(VEHICLE_REGION_DECIMALS, regionDecimals);

    }
}
