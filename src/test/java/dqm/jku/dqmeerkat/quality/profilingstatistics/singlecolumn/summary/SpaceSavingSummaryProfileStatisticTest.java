package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

class SpaceSavingSummaryProfileStatisticTest {


    private static Attribute dsdElement;
    private static RecordList recordList;

    @BeforeEach
    void setup() throws IOException {
        var conn = FileSelectionUtil.getConnectorCSV("src/test/resources/testRecordList.csv");
        var ds = conn.loadSchema("http:/example.com", "hum");
        var concept = ds.getConcepts().stream().findFirst().orElseThrow();
        dsdElement = concept.getAttribute("values");
        recordList = conn.getRecordList(concept);
    }

    @Test
    void calculation() throws NoSuchMethodException {
        // given
        var k = 10;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);

        // when
        spaceSavingSummary.calculation(recordList, null);
        var ret = spaceSavingSummary.getValue();

        // then
        Assertions.assertNotNull(ret);
        Assertions.assertNotNull(spaceSavingSummary.getValueClass());
        Assertions.assertEquals(HashMap.class, spaceSavingSummary.getValueClass());
        Assertions.assertEquals(k, ret.size());
    }

    @Test
    void calculationStringOneValue() throws NoSuchMethodException, IOException {
        // given
        var conn = FileSelectionUtil.getConnectorCSV("src/test/resources/testRecordList.csv");
        var ds = conn.loadSchema("http:/example.com", "hum");
        var concept = ds.getConcepts().stream().findFirst().orElseThrow();
        var stringDsdElement = concept.getAttribute("valueMetadataId");
        var stringRecordList = conn.getRecordList(concept);
        var k = 10;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(stringRecordList, stringDsdElement), k);

        // when
        spaceSavingSummary.calculation(stringRecordList, null);
        var ret = spaceSavingSummary.getValue();

        // then
        Assertions.assertNotNull(ret);
        Assertions.assertNotNull(spaceSavingSummary.getValueClass());
        Assertions.assertEquals(HashMap.class, spaceSavingSummary.getValueClass());
        Assertions.assertEquals(1, ret.size());
    }

    @Test
    void calculationString() throws NoSuchMethodException, IOException {
        // given
        var conn = FileSelectionUtil.getConnectorCSV("src/test/resources/testRecordList.csv");
        var ds = conn.loadSchema("http:/example.com", "hum");
        var concept = ds.getConcepts().stream().findFirst().orElseThrow();
        var stringDsdElement = concept.getAttribute("timestamp");
        var stringRecordList = conn.getRecordList(concept);
        var k = 10;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(stringRecordList, stringDsdElement), k);

        // when
        spaceSavingSummary.calculation(stringRecordList, null);
        var ret = spaceSavingSummary.getValue();

        // then
        Assertions.assertNotNull(ret);
        Assertions.assertNotNull(spaceSavingSummary.getValueClass());
        Assertions.assertEquals(HashMap.class, spaceSavingSummary.getValueClass());
        Assertions.assertEquals(k, ret.size());
    }

    @Test
    void getValueUninitialized() throws NoSuchMethodException {
        // given
        var k = 10;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        // when
        var ret = spaceSavingSummary.getValue();
        // then
        Assertions.assertNull(ret);

    }

    @Test
    void getValueClass() throws NoSuchMethodException {
        // given
        var k = 10;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        // when
        var ret = spaceSavingSummary.getValueClass();

        // then
        Assertions.assertNotNull(ret);
        Assertions.assertEquals(HashMap.class, ret);
    }


    @Test
    void checkConformance() throws NoSuchMethodException {
        // given
        var k = 5;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        var spaceSavingSummary2 = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        var data = new RecordList(List.of(1, 1, 3, 2, 3, 6, 7, 1, 9, 6), "data");
        var data2 = new RecordList(List.of(1, 1, 1, 2, 3, 6, 7, 1, 9, 6), "data2");
        spaceSavingSummary.calculation(data, null);
        spaceSavingSummary2.calculation(data2, null);

        // when
        var ret = spaceSavingSummary.checkConformance(spaceSavingSummary2, 0.1);

        // then
        Assertions.assertTrue(ret);
    }

    @Test
    void checkConformanceEqual() throws NoSuchMethodException {
        // given
        var k = 5;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        var spaceSavingSummary2 = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        var data = new RecordList(List.of(1, 1, 3, 2, 3, 6, 7, 1, 9, 6), "data");
        var data2 = new RecordList(List.of(1, 1, 1, 2, 3, 6, 7, 1, 9, 6), "data2");
        spaceSavingSummary.calculation(data, null);
        spaceSavingSummary2.calculation(data2, null);

        // when
        var ret = spaceSavingSummary.checkConformance(spaceSavingSummary2, 0.1);

        // then
        Assertions.assertTrue(ret);
    }

    @Test
    void checkConformanceFail() throws NoSuchMethodException {
        // given
        var k = 5;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        var spaceSavingSummary2 = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        List<Number> data = List.of(1, 1, 3, 2, 3, 6, 7, 1, 9, 6);
        List<Number> data2 = List.of(6, 7, 5, 5, 7, 6, 7, 7, 6, 9, 9, 9, 9, 9, 9);
        spaceSavingSummary.calculation(new RecordList(data, "data"), null);
        spaceSavingSummary2.calculation(new RecordList(data2, "data2"), null);

        // when
        var ret = spaceSavingSummary.checkConformance(spaceSavingSummary2, 0.1);

        // then
        Assertions.assertFalse(ret);
    }
}