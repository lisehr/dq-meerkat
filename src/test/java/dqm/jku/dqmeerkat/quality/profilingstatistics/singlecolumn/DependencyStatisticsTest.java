package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.dependency.*;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class tests profile statistics that are in the package {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.dependency}.
 * The tests are run with the "id", the "region" and/or the "lat" column of the vehicles30000.csv
 * @author Johannes Schrott
 */

class DependencyStatisticsTest {

    private static DataProfile vehicleIdDP;
    private static DataProfile vehicleRegionDP;
    private static DataProfile vehicleLatitudeDP;
    private static RecordList vehicleRecords;



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

        } catch (IOException e) {
            e.printStackTrace();
        }


        assertNotNull(vehicleIdDP);
        assertNotNull(vehicleRegionDP);
        assertNotNull(vehicleLatitudeDP);

    }

    @Test
    @DisplayName("KeyCandidate (Boolean Value)")
    void testKeyCandidateValue() {
        vehicleLatitudeDP.addStatistic(new KeyCandidate(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Boolean latitudeKeyCandidate = (Boolean) vehicleLatitudeDP.getStatistic(StatisticTitle.keyCand).getValue();

        assertEquals(false, latitudeKeyCandidate);


        vehicleRegionDP.addStatistic(new KeyCandidate(vehicleRegionDP));
        vehicleRegionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Boolean regionKeyCandidate = (Boolean) vehicleRegionDP.getStatistic(StatisticTitle.keyCand).getValue();

        assertEquals(false, regionKeyCandidate);


        // The id column is a primary key, therefore it is a key candidate

        vehicleIdDP.addStatistic(new KeyCandidate(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Boolean idKeyCandidate = (Boolean) vehicleIdDP.getStatistic(StatisticTitle.keyCand).getValue();

        assertEquals(true, idKeyCandidate);
    }

    @Test
    @DisplayName("KeyCandidate (Numeric Value)")
    void testKeyCandidateNumericValue() {
        vehicleLatitudeDP.addStatistic(new KeyCandidate(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Number latitudeKeyCandidate = (Number) vehicleLatitudeDP.getStatistic(StatisticTitle.keyCand).getNumericVal();

        assertEquals(0, latitudeKeyCandidate);


        vehicleRegionDP.addStatistic(new KeyCandidate(vehicleRegionDP));
        vehicleRegionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Number regionKeyCandidate = (Number) vehicleRegionDP.getStatistic(StatisticTitle.keyCand).getNumericVal();

        assertEquals(0, regionKeyCandidate);


        // The id column is a primary key, therefore it is a key candidate

        vehicleIdDP.addStatistic(new KeyCandidate(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Number idKeyCandidate = (Number) vehicleIdDP.getStatistic(StatisticTitle.keyCand).getNumericVal();

        assertEquals(1, idKeyCandidate);
    }
}
