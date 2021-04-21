package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValues;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValuesPercentage;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.Uniqueness;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * This class tests profile metrics that are in the package {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality}.
 * The tests are run with the "Count" column of the Popular_Baby_Names.csv
 * @author Johannes Schrott
 */

@DisplayName("ProfileStatistics: SingleColumn: Cardinality")
class TestCardinalityStatistics {

    static final int NUMBER_OF_RECORDS = 19418; // according to excel
    static final int CARDINALITY = 280; // According to excel

    DataProfile babyNamesCountDP;
    RecordList records;

    @BeforeEach
    @DisplayName("Create a data profile for the Popular_Baby_Names.csv column \"Count\"")
    void setUp() {
        try {
            babyNamesCountDP = new DataProfile();
            DSConnector csvConnector = FileSelectionUtil.getConnectorCSV(Constants.FileName.popularBabyNames.getPath());
            Datasource ds = csvConnector.loadSchema();
            ds.getConcepts().forEach(concept -> {
                Attribute attribute = concept.getAttribute("Count");
                babyNamesCountDP.setElem(attribute);
                babyNamesCountDP.setURI(attribute.getURI() + "/profile");
                try {
                    records = csvConnector.getRecordList(concept);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(babyNamesCountDP);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows} profile statistic.
     * */
    @Test
    @DisplayName("NumRows")
    void testNumRows() {
        babyNamesCountDP.addStatistic(new NumRows(babyNamesCountDP));
        babyNamesCountDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        Long numRows = (Long) babyNamesCountDP.getStatistic(StatisticTitle.numrows).getValue();

        assertEquals(NUMBER_OF_RECORDS, numRows);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValues} profile statistic.
     * */
    @Test
    @DisplayName("NullValues (there exist no null values)")
    void testNullValues() {
        babyNamesCountDP.addStatistic(new NullValues(babyNamesCountDP));
        babyNamesCountDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        Long nrOfNullValues = (Long) babyNamesCountDP.getStatistic(StatisticTitle.nullVal).getValue();

        assertEquals(0, nrOfNullValues); // when opening the popular Baby Names in excel we can determine that it has no null values (= empty values) in the Count column
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValuesPercentage} profile statistic.
     * */
    @Test
    @DisplayName("NullValuesPercentage (there exist no null values)")
    void testNullValuesPercentage() {
        babyNamesCountDP.addStatistic(new NullValuesPercentage(babyNamesCountDP));
        babyNamesCountDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        // The profilemetrics computed value is a Double
        assertEquals(Double.class, babyNamesCountDP.getStatistic(StatisticTitle.nullValP).getValueClass());

        Double percentageOfNullValues = (Double) babyNamesCountDP.getStatistic(StatisticTitle.nullValP).getValue();

        assertEquals(0, percentageOfNullValues); // when opening the popular Baby Names in excel we can determine that it has no null values (= empty values) in the Count column
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.Cardinality} profile statistic.
     * */
    @Test
    @DisplayName("Cardinality")
    void testCardinalityCount() {
        babyNamesCountDP.addStatistic(new Uniqueness(babyNamesCountDP));
        babyNamesCountDP.getStatistics().forEach(statistic -> statistic.calculation(records, null));

        // The profilemetrics computed value is a Long
        assertEquals(Long.class, babyNamesCountDP.getStatistic(StatisticTitle.card).getValueClass());

        Long uniqueValues = (Long) babyNamesCountDP.getStatistic(StatisticTitle.card).getValue();

        assertEquals(CARDINALITY, uniqueValues);
    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.Uniqueness} profile statistic.
     * */
    @Test
    @DisplayName("Uniqueness")
    void testUniqueness() {

    }
}
