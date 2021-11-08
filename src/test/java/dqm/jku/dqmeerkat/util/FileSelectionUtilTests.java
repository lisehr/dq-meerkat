package dqm.jku.dqmeerkat.util;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileSelectionUtilTests {
    @Test
    @DisplayName("Connect to CSV with PATH (exemplarily with Popular Baby Names CSV)")
    void testGetCSVConnectorWithPath() {
       // try {
        assertDoesNotThrow(() -> {
            FileSelectionUtil.getConnectorCSV(Constants.FileName.acceleration.getPath());
        });

        try {
            ConnectorCSV csvConnector = FileSelectionUtil.getConnectorCSV(Constants.FileName.popularBabyNames.getPath());
            assertEquals("src/main/java/dqm/jku/dqmeerkat/resources/csv/Popular_Baby_Names.csv", csvConnector.filename, "The Popular_Baby_Names.csv has not been found in the resources/csv folder. Check if it is there.");
            assertEquals(",", csvConnector.separator);
            assertEquals("\n", csvConnector.linebreak);
            assertEquals("Popular_Baby_Names", csvConnector.label); // This is important to be correct for the KG!!
            assertTrue(csvConnector.removeQuotes);
        } catch (IOException e) {
        // It was already checked that for the file which is used for this test no exception is thrown
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Connect to CSV via Index (deprecated) and check which file is addressed (exemplarily: Acceleration.csv)")
    void testGetCSVConnectorWithIndex() {
        assertDoesNotThrow(() -> {
            FileSelectionUtil.getConnectorCSV(1);
        });

        try {
            // deprecated Method, but still used at some points;
            ConnectorCSV csvConnector = FileSelectionUtil.getConnectorCSV(1);

            // Index 1 has to point to Acceleration.csv
            assertTrue(csvConnector.filename.endsWith("Acceleration.csv"),"Connecting to CSV via index works, but in the resources folder there are some files missing or to much files in it. Check what files in your resources folder are, index 1 (= the second file alphabetically should be \"Acceleration.csv\"");
            assertEquals(",", csvConnector.separator);
            assertEquals("\n", csvConnector.linebreak);
            assertEquals("Acceleration", csvConnector.label); // This is important to be correct for the KG!!
            assertTrue(csvConnector.removeQuotes);
        } catch (IOException e) {
            // It was already checked that for the file which is used for this test no exception is thrown
            e.printStackTrace();
        }
    }
}
