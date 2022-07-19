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
import org.junit.jupiter.api.*;

import java.io.IOException;

/**
 * This class tests profile statistics that are in the package {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality}.
 * The tests are run with the "id" and the "condition" column of the vehicles30000.csv and the emoty.csv
 * @author Johannes Schrott
 */

@DisplayName("ProfileStatistics: SingleColumn: Cardinality")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CardinalityStatisticsTest {

    // The expected results for the tests where calculated using the vehicles30000.xlsx
    static final int NUMBER_OF_RECORDS_VEHICLE = 29759;
    static final int CARDINALITY = 29759;
    static final double UNIQUENSS = ((double) CARDINALITY/(double) NUMBER_OF_RECORDS_VEHICLE)*100;
    static final int NUMBER_OF_NULL_ID = 0; // Number of null values in "id" column
    static final int NUMBER_OF_NULL_CONDITION = 12112;

    static final int NUMBER_OF_RECORDS_EMPTY = 7;
    static final int NUMBER_OF_NULL_EMPTY = 7; // in the empty.csv (has 1 column) all 7 records are null

    static DataProfile vehicleIdDP;
    static DataProfile vehicleConditionDP;
    static RecordList vehicleRecords;

    static DataProfile emptyDP;
    static RecordList emptyRecords;

    @BeforeAll
    @DisplayName("Create a data profile for the vehicles30000.csv column \"id\"")
    static void setUp() {
        try {
            vehicleIdDP = new DataProfile();
            vehicleConditionDP = new DataProfile();

            DSConnector csvConnector = FileSelectionUtil.getConnectorCSV(Constants.FileName.vehicles.getPath());
            Datasource ds = csvConnector.loadSchema();
            ds.getConcepts().forEach(concept -> {
                Attribute attribute = concept.getAttribute("id");
                vehicleIdDP.setElem(attribute);
                vehicleIdDP.setURI(attribute.getURI() + "/profile");

                attribute = concept.getAttribute("condition");
                vehicleConditionDP.setElem(attribute);
                vehicleConditionDP.setURI(attribute.getURI() + "/profile");

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
                Attribute attribute = concept.getAttribute("emptycolumn"); // Spaces in names of a column (attribute) get removed...
                emptyDP.setElem(attribute);
                emptyDP.setURI(attribute.getURI()+"/profile");
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
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows} profile statistic.
     * */
    @Test
    @DisplayName("NumRows")
    void testNumRows() {
        vehicleIdDP.addStatistic(new NumRows(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long numRows = (Long) vehicleIdDP.getStatistic(StatisticTitle.numrows).getValue();

        assertEquals(NUMBER_OF_RECORDS_VEHICLE, numRows);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValues} profile statistic.
     * */
    @Test
    @DisplayName("NullValues")
    void testNullValues() {
        vehicleIdDP.addStatistic(new NullValues(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long nrOfNullValuesVehicle = (Long) vehicleIdDP.getStatistic(StatisticTitle.nullVal).getValue();

        assertEquals(NUMBER_OF_NULL_ID, nrOfNullValuesVehicle);



        emptyDP.addStatistic(new NullValues(emptyDP));
        emptyDP.getStatistics().forEach(statistic -> statistic.calculation(emptyRecords, null));

        Long nrOfNullValuesEmpty = (Long) emptyDP.getStatistic(StatisticTitle.nullVal).getValue();

        assertEquals(NUMBER_OF_NULL_EMPTY, nrOfNullValuesEmpty);



        vehicleConditionDP.addStatistic(new NullValues(vehicleConditionDP));
        vehicleConditionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        Long nrOfNullValuesCondition = (Long) vehicleConditionDP.getStatistic(StatisticTitle.nullVal).getValue();

        assertEquals(NUMBER_OF_NULL_CONDITION, nrOfNullValuesCondition);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValuesPercentage} profile statistic.
     * */
    @Test
    @DisplayName("NullValuesPercentage")
    void testNullValuesPercentage() {
        vehicleIdDP.addStatistic(new NullValuesPercentage(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The profile statistics computed value is a Double
        assertEquals(Double.class, vehicleIdDP.getStatistic(StatisticTitle.nullValP).getInputValueClass());

        Double percentageOfNullValuesVehicle = (Double) vehicleIdDP.getStatistic(StatisticTitle.nullValP).getValue();

        assertEquals(((double) NUMBER_OF_NULL_ID)/((double) NUMBER_OF_RECORDS_VEHICLE)*100 , percentageOfNullValuesVehicle); // when opening the popular Baby Names in excel we can determine that it has no null values (= empty values) in the Count column




        vehicleConditionDP.addStatistic(new NullValuesPercentage(vehicleConditionDP));
        vehicleConditionDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The profile statistics computed value is a Double
        assertEquals(Double.class, vehicleConditionDP.getStatistic(StatisticTitle.nullValP).getInputValueClass());

        Double percentageOfNullValuesCondition = (Double) vehicleConditionDP.getStatistic(StatisticTitle.nullValP).getValue();

        assertEquals(((double) NUMBER_OF_NULL_CONDITION)/((double) NUMBER_OF_RECORDS_VEHICLE)*100 , percentageOfNullValuesCondition);



        emptyDP.addStatistic(new NullValuesPercentage(emptyDP));
        emptyDP.getStatistics().forEach(statistic -> statistic.calculation(emptyRecords, null));

        // The profile statistics computed value is a Double
        assertEquals(Double.class, emptyDP.getStatistic(StatisticTitle.nullValP).getInputValueClass());

        Double percentageOfNullValuesEmpty = (Double) emptyDP.getStatistic(StatisticTitle.nullValP).getValue();

        assertEquals(((double) NUMBER_OF_NULL_EMPTY)/((double) NUMBER_OF_RECORDS_EMPTY)*100 , percentageOfNullValuesEmpty);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.Cardinality} profile statistic.
     * */
    @Test
    @DisplayName("Cardinality")
    void testCardinalityCount() {
        vehicleIdDP.addStatistic(new Cardinality(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The profilemetrics computed value is a Long
        assertEquals(Long.class, vehicleIdDP.getStatistic(StatisticTitle.card).getInputValueClass());

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
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The profilemetrics computed value is a Double
        assertEquals(Double.class, vehicleIdDP.getStatistic(StatisticTitle.unique).getInputValueClass());

        Double uniquenessPercentage = (Double) vehicleIdDP.getStatistic(StatisticTitle.unique).getValue();

        assertEquals(UNIQUENSS, uniquenessPercentage);
    }
}
