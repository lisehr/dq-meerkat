package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.*;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * This class tests profile metrics that are in the package {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality}.
 * The tests are run with the "Count" column of the vehicles30000.csv
 * @author Johannes Schrott
 */

@DisplayName("ProfileStatistics: SingleColumn: Cardinality")
class CardinalityStatisticsTest {

    // The expected results for the tests where calculated using LibreOffice Calc (Excel has troubles with CSV files)
    static final int NUMBER_OF_RECORDS = 29759;
    static final int CARDINALITY = 29759;
    static final double UNIQUENSS = ((double) CARDINALITY/(double) NUMBER_OF_RECORDS)*100;
    static final int NUMBER_OF_NULL_ID = 0; // Number of null values in "id" column

    static DataProfile vehicleIdDP;
    static RecordList records;

    @BeforeAll
    @DisplayName("Create a data profile for the vehicles30000.csv column \"id\"")
    static void setUp() {
        try {
            vehicleIdDP = new DataProfile();
            DSConnector csvConnector = FileSelectionUtil.getConnectorCSV(Constants.FileName.vehicles.getPath());
            Datasource ds = csvConnector.loadSchema();
            ds.getConcepts().forEach(concept -> {
                Attribute attribute = concept.getAttribute("id");
                vehicleIdDP.setElem(attribute);
                vehicleIdDP.setURI(attribute.getURI() + "/profile");
                try {
                    records = csvConnector.getRecordList(concept);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(vehicleIdDP);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows} profile statistic.
     * */
    @Test
    @DisplayName("NumRows")
    void testNumRows() {
        vehicleIdDP.addStatistic(new NumRows(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        Long numRows = (Long) vehicleIdDP.getStatistic(StatisticTitle.numrows).getValue();

        assertEquals(NUMBER_OF_RECORDS, numRows);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValues} profile statistic.
     * */
    @Test
    @DisplayName("NullValues")
    void testNullValues() {
        vehicleIdDP.addStatistic(new NullValues(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        Long nrOfNullValues = (Long) vehicleIdDP.getStatistic(StatisticTitle.nullVal).getValue();

        assertEquals(NUMBER_OF_NULL_ID, nrOfNullValues); // when opening the popular Baby Names in excel we can determine that it has no null values (= empty values) in the Count column
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValuesPercentage} profile statistic.
     * */
    @Test
    @DisplayName("NullValuesPercentage (there exist no null values)")
    void testNullValuesPercentage() {
        vehicleIdDP.addStatistic(new NullValuesPercentage(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        // The profilemetrics computed value is a Double
        assertEquals(Double.class, vehicleIdDP.getStatistic(StatisticTitle.nullValP).getValueClass());

        Double percentageOfNullValues = (Double) vehicleIdDP.getStatistic(StatisticTitle.nullValP).getValue();

        assertEquals(((double) NUMBER_OF_NULL_ID)/((double) NUMBER_OF_RECORDS)*100 , percentageOfNullValues); // when opening the popular Baby Names in excel we can determine that it has no null values (= empty values) in the Count column
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.Cardinality} profile statistic.
     * */
    @Test
    @DisplayName("Cardinality")
    void testCardinalityCount() {
        vehicleIdDP.addStatistic(new Cardinality(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        // The profilemetrics computed value is a Long
        assertEquals(Long.class, vehicleIdDP.getStatistic(StatisticTitle.card).getValueClass());

        Long uniqueValues = (Long) vehicleIdDP.getStatistic(StatisticTitle.card).getValue();

        assertEquals(CARDINALITY, uniqueValues);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.Uniqueness} profile statistic.
     * */
    @Test
    @DisplayName("Uniqueness")
    void testUniqueness() {
        // Test is not implemented yet.
        vehicleIdDP.addStatistic(new Uniqueness(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        // The profilemetrics computed value is a Double
        assertEquals(Double.class, vehicleIdDP.getStatistic(StatisticTitle.unique).getValueClass());

        Double uniquenessPercentage = (Double) vehicleIdDP.getStatistic(StatisticTitle.unique).getValue();

        assertEquals(UNIQUENSS, uniquenessPercentage);
    }
}
