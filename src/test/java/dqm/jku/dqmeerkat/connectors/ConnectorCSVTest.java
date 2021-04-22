package dqm.jku.dqmeerkat.connectors;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class tests some aspects of {@link dqm.jku.dqmeerkat.connectors.ConnectorCSV}.
 * @author Johannes Schrott
 */

public class ConnectorCSVTest {
    static final int NUMBER_OF_RECORDS = 3; // according to excel

    DataProfile vehicleIdDP;
    RecordList records;

    /**
     * Tests if quoted newlines are ignored. E.g. "this is not\na new line" should only trigger the creation of one records, not of two records.
     */
    @Test
    @DisplayName("New lines in a quotation are escaped correctly (GitHub Issue #12)")
    void testQuotedNewline() {
        try {
            vehicleIdDP = new DataProfile();
            DSConnector csvConnector = FileSelectionUtil.getConnectorCSV(Constants.FileName.vehiclesSmall.getPath());
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
        assertNotNull(records);
        assertEquals(NUMBER_OF_RECORDS, records.size(),
                "The quoted newlines in the file vehicles3.csv were not recognized correctly, " +
                        "therefore the number of records is not the expected one. " +
                        "If Constants.ESCAPE_QUOTED_NEWLINES is set to FALSE then this always fails.");
    }
}
