package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.histogram.FrequencyClass;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.histogram.Histogram;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.histogram.SerializableFrequencyMap;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class tests the histogram profile statistic {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.histogram.Histogram}.
 * The tests are run with the "id", the "region" and/or the "lat" column of the vehicles30000.csv
 * @author Johannes Schrott
 */

@DisplayName("ProfileStatistics: SingleColumn: Histogram")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class HistogramStatisticTest {

    private static DataProfile vehicleIdDP;
    private static DataProfile vehicleRegionDP;
    private static DataProfile vehicleLatitudeDP;
    private static RecordList vehicleRecords;

    // Expected values, calculated with Excel
    private final static long VEHICLE_ID_MINIMUM = 7218891961L;
    private final static long VEHICLE_ID_MAXIMUM = 7240681620L;

    private final static double VEHICLE_LATITUDE_MAXIMUM = 64.993698;
    private final static double VEHICLE_LATITUDE_MINIMUM = -79.80964;

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
    @DisplayName("Histogram (Double)")
    void testHistogramDouble() {
        vehicleLatitudeDP.addStatistic(new Histogram(vehicleLatitudeDP));
        vehicleLatitudeDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The histogram is of type Histogram
        assertEquals(Histogram.class, vehicleLatitudeDP.getStatistic(StatisticTitle.hist).getClass());

        // The "value" of the Histogram has to be a SerializableFrequencyMap
        assertEquals(SerializableFrequencyMap.class, vehicleLatitudeDP.getStatistic(StatisticTitle.hist).getValue().getClass());

        double k = Math.ceil(1+3.32*Math.log10(vehicleRecords.size())); // Usage of Math.ceil ensures that all records are covered by the histogramm
        double classrange = (VEHICLE_LATITUDE_MAXIMUM-VEHICLE_LATITUDE_MINIMUM) / k;

        Histogram hist = (Histogram) vehicleLatitudeDP.getStatistic(StatisticTitle.hist);
        assertEquals(VEHICLE_LATITUDE_MINIMUM, hist.getMin().doubleValue());
        assertEquals(VEHICLE_LATITUDE_MAXIMUM, hist.getMax().doubleValue());
        assertEquals(k, hist.getNumberOfClasses());
        assertEquals(classrange, hist.getClassrange().doubleValue());

        double[] lats = vehicleRecords.toList().stream()
                .filter(vehicleRecord -> vehicleRecord.getField("lat") != null)
                .mapToDouble(vehicleRecord -> (Double) vehicleRecord.getField("lat"))
                .sorted()
                .toArray();

        StringBuilder sb = new StringBuilder();
        // now check if the number of elements in each class is computed correctly.
        for (int i = 0; i < k; i++) {
            double lowerBound = VEHICLE_LATITUDE_MINIMUM + (i*classrange);
            double upperBound = VEHICLE_LATITUDE_MINIMUM + (i+1)*classrange;
            long number = Arrays.stream(lats).filter(value -> lowerBound <= value && value <= upperBound).count();
            sb.append(number);
            sb.append("-");
        }

        assertEquals(sb.toString(), hist.getClassValues());
    }

    @Test
    @DisplayName("Histogram (Long)")
    void testHistogramLong() {
        vehicleIdDP.addStatistic(new Histogram(vehicleIdDP));
        vehicleIdDP.getStatistics().forEach(statistic -> statistic.calculation(vehicleRecords, null));

        // The histogram is of type Histogram
        assertEquals(Histogram.class, vehicleIdDP.getStatistic(StatisticTitle.hist).getClass());

        // The "value" of the Histogram has to be a SerializableFrequencyMap
        assertEquals(SerializableFrequencyMap.class, vehicleIdDP.getStatistic(StatisticTitle.hist).getValue().getClass());

        double k = Math.ceil(1+3.32*Math.log10(vehicleRecords.size())); // Usage of Math.ceil ensures that all records are covered by the histogramm
        double classrange = (VEHICLE_ID_MAXIMUM-VEHICLE_ID_MINIMUM) / k;

        Histogram hist = (Histogram) vehicleIdDP.getStatistic(StatisticTitle.hist);
        assertEquals(VEHICLE_ID_MINIMUM, hist.getMin().longValue());
        assertEquals(VEHICLE_ID_MAXIMUM, hist.getMax().longValue());
        assertEquals(k, hist.getNumberOfClasses());
        assertEquals(classrange, hist.getClassrange().doubleValue());

        long[] ids = vehicleRecords.toList().stream()
                .mapToLong(vehicleRecord -> (Long) vehicleRecord.getField("id"))
                .sorted()
                .toArray();

        StringBuilder sb = new StringBuilder();
        // now check if the number of elements in each class is computed correctly.
        for (int i = 0; i < k; i++) {
            double lowerBound = VEHICLE_ID_MINIMUM + (i*classrange);
            double upperBound = VEHICLE_ID_MINIMUM + (i+1)*classrange;
            long number = Arrays.stream(ids).filter(value -> lowerBound <= value && value <= upperBound).count();
            sb.append(number);
            sb.append("-");
        }

        assertEquals(sb.toString(), hist.getClassValues());
    }
}
