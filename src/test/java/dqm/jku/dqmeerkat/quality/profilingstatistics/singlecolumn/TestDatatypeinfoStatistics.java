package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.BasicType;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.Maximum;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class tests profile metrics that are in the package {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo}.
 * The tests are run with the "Count" column of the Popular_Baby_Names.csv, and with the \"Unit Cost\" column of the 100000 sales records.
 * @author Johannes Schrott
 */

@DisplayName("ProfileStatistics: SingleColumn: Datatypeinfo")
class TestDatatypeinfoStatistics {


    DataProfile babyNamesCountDP;
    DataProfile babyNamesNameDP;
    DataProfile salesRecordsUnitCostsDP;
    RecordList recordsBabyNames;
    RecordList recordsSalesRecords;


    @BeforeEach
    @DisplayName("Create data profiles for the Popular_Baby_Names.csv columns \"Count\" and \"Child's First Name\"")
    void setUp() {
        try {
            babyNamesCountDP = new DataProfile();
            babyNamesNameDP = new DataProfile();
            DSConnector csvConnectorBabyNames = FileSelectionUtil.getConnectorCSV(Constants.FileName.popularBabyNames.getPath());
            Datasource ds = csvConnectorBabyNames.loadSchema();
            ds.getConcepts().forEach(concept -> {
                Attribute attributeCount = concept.getAttribute("Count");
                babyNamesCountDP.setElem(attributeCount);
                babyNamesCountDP.setURI(attributeCount.getURI() + "/profile");

                Attribute attributeName = concept.getAttribute("Child'sFirstName");
                babyNamesNameDP.setElem(attributeName);
                babyNamesNameDP.setURI(attributeName.getURI() + "/profile");

                try {
                    recordsBabyNames = csvConnectorBabyNames.getRecordList(concept);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            salesRecordsUnitCostsDP = new DataProfile();
            DSConnector csvConnectorSalesRecords = FileSelectionUtil.getConnectorCSV(Constants.FileName.salesRecords.getPath());
            ds = csvConnectorSalesRecords.loadSchema();
            ds.getConcepts().forEach(concept -> {
                Attribute attributeCount = concept.getAttribute("UnitCost");
                salesRecordsUnitCostsDP.setElem(attributeCount);
                salesRecordsUnitCostsDP.setURI(attributeCount.getURI() + "/profile");

                try {
                    recordsSalesRecords = csvConnectorSalesRecords.getRecordList(concept);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(babyNamesCountDP);
        assertNotNull(babyNamesNameDP);

        assertNotNull(salesRecordsUnitCostsDP);

    }

    /**
     * This method tests the {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.BasicType} profile statistic.
     * */
    @Test
    @DisplayName("BasicType (no null values)")
    void testBasicType() {
        babyNamesCountDP.addStatistic(new BasicType(babyNamesCountDP));
        babyNamesNameDP.addStatistic(new BasicType(babyNamesNameDP));
        babyNamesCountDP.getStatistics().forEach(statistic -> statistic.calculation(recordsBabyNames, null));
        babyNamesNameDP.getStatistics().forEach(statistic -> statistic.calculation(recordsBabyNames, null));

        // The profilestatistics computed value is a String (this is not the type of the attributes values!)
        assertEquals(String.class, babyNamesCountDP.getStatistic(StatisticTitle.bt).getValueClass());
        assertEquals(String.class, babyNamesCountDP.getStatistic(StatisticTitle.bt).getValueClass());

        String countType = (String) babyNamesCountDP.getStatistic(StatisticTitle.bt).getValue();
        String nameType = (String) babyNamesNameDP.getStatistic(StatisticTitle.bt).getValue();

        assertEquals("Numeric", countType);
        assertEquals("String", nameType);

    }

    @Test
    @DisplayName("Maximum")
    void testMaximum() {
        salesRecordsUnitCostsDP.addStatistic(new Maximum(salesRecordsUnitCostsDP));
        salesRecordsUnitCostsDP.getStatistics().forEach(statistic -> statistic.calculation(recordsSalesRecords, null));

        Double maxUnitCosts = (Double) salesRecordsUnitCostsDP.getStatistic(StatisticTitle.max).getValue();

        assertEquals(524.96, maxUnitCosts);
    }

}
