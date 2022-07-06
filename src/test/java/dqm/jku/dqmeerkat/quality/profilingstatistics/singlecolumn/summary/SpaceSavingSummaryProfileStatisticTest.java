package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SpaceSavingSummaryProfileStatisticTest {


    private static Attribute dsdElement;
    private static RecordList recordList;

    @BeforeAll
    static void setup() throws IOException {
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
        var ret = (Map<Object, Integer>) spaceSavingSummary.getValue();

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
    void calculationNumericSimple() throws NoSuchMethodException {
        // given
        var k = 10;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        List<Number> data = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var expected = Map.of(1, 1, 2, 1, 3, 1, 4, 1, 5, 1,
                6, 1, 7, 1, 8, 1, 9, 1, 10, 1);
        // when
        spaceSavingSummary.calculationNumeric(data, null);
        Map<Object, Integer> ret = (Map<Object, Integer>) spaceSavingSummary.getValue();

        // then
        Assertions.assertNotNull(ret);
        Assertions.assertEquals(k, ret.size());
        Assertions.assertEquals(expected, ret);
    }


    @Test
    void calculationNumeric() throws NoSuchMethodException {
        // given
        var k = 5;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        List<Number> data = List.of(1, 1, 3, 2, 3, 6, 7, 1, 9, 6);
        var expected = new HashMap<>();
        expected.put(1, 3);
        expected.put(3, 2);
        expected.put(6, 2);
        expected.put(7, 1);
        expected.put(9, 1);
        // when
        spaceSavingSummary.calculationNumeric(data, null);
        Map<Object, Integer> ret = (Map<Object, Integer>) spaceSavingSummary.getValue();

        // then
        Assertions.assertNotNull(ret);
        Assertions.assertEquals(k, ret.size());
        Assertions.assertEquals(expected, ret);
    }

    @Test
    void checkConformance() throws NoSuchMethodException {
        // given
        var k = 5;
        var spaceSavingSummary = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        var spaceSavingSummary2 = new SpaceSavingSummaryProfileStatistic(new DataProfile(recordList, dsdElement), k);
        List<Number> data = List.of(1, 1, 3, 2, 3, 6, 7, 1, 9, 6);
        List<Number> data2 = List.of(1, 1, 1, 2, 3, 6, 7, 1, 9, 6);
        spaceSavingSummary.calculationNumeric(data, null);
        spaceSavingSummary2.calculationNumeric(data2, null);

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
        List<Number> data = List.of(1, 1, 3, 2, 3, 6, 7, 1, 9, 6);
        List<Number> data2 = List.of(1, 1, 1, 2, 3, 6, 7, 1, 9, 6);
        spaceSavingSummary.calculationNumeric(data, null);
        spaceSavingSummary2.calculationNumeric(data2, null);

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
        List<Number> data2 = List.of(6, 7, 5, 5, 7, 9, 8, 7, 6, 9);
        spaceSavingSummary.calculationNumeric(data, null);
        spaceSavingSummary2.calculationNumeric(data2, null);

        // when
        var ret = spaceSavingSummary.checkConformance(spaceSavingSummary2, 0.1);

        // then
        Assertions.assertFalse(ret);
    }
}