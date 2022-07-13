package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class MGSummaryProfileStatisticTest {


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
    void calculationTest() throws NoSuchMethodException {
        // given
        var statistic = new MGSummaryProfileStatistic(new DataProfile(recordList, dsdElement), 50);

        // when
        statistic.calculation(recordList, null);
        // then
        var ret = statistic.getValue();
        Assertions.assertEquals(40, ret.size());
        Assertions.assertEquals(4, ret.get(22D));
    }

    @Test
    void calculationNumericTest() throws NoSuchMethodException {
        // given
        var statistic = new MGSummaryProfileStatistic(new DataProfile(recordList, dsdElement), 50);

        // when
        statistic.calculation(recordList, null);
        // then
        var ret = statistic.getValue();
        Assertions.assertEquals(40, ret.size());
        Assertions.assertEquals(4, ret.get(22D));
    }

    @Test
    void checkConformanceSuccessTest() throws NoSuchMethodException {
        // given
        var statistic = new MGSummaryProfileStatistic(new DataProfile(recordList, dsdElement), 50);
        var statistic2 = new MGSummaryProfileStatistic(new DataProfile(recordList, dsdElement), 50);
        statistic.calculation(recordList, null);
        statistic2.calculation(recordList, null);

        // when
        var ret = statistic.checkConformance(statistic2, 0.1);
        // then
        Assertions.assertTrue(ret);
    }

    @Test
    void checkConformanceFailTest() throws NoSuchMethodException {
        // given
        var statistic = new MGSummaryProfileStatistic(new DataProfile(recordList, dsdElement), 50);
        var statistic2 = new MGSummaryProfileStatistic(new DataProfile(recordList, dsdElement), 10);
        statistic.calculation(recordList, null);
        statistic2.calculation(recordList, null);

        // when
        var ret = statistic.checkConformance(statistic2, 0.1);

        // then
        Assertions.assertFalse(ret);
    }

    @Test
    void checkConformanceBarelyConformingTest() throws NoSuchMethodException {
        // given
        var data = new RecordList(List.of(1, 3, 3, 3, 3,
                2, 3, 2, 5, 7,
                8, 1, 2, 3, 6), "data");
        var data2 = new RecordList(List.of(1, 3, 1, 3, 3,
                2, 1, 6, 5, 3,
                1, 8, 2, 1, 1), "data2");

        var statistic = new MGSummaryProfileStatistic(new DataProfile(recordList, dsdElement), 5);
        var statistic2 = new MGSummaryProfileStatistic(new DataProfile(recordList, dsdElement), 5);
        statistic.calculation(data, null);
        statistic2.calculation(data2, null);

        // when
        var ret = statistic.checkConformance(statistic2, 0.3);

        // then
        Assertions.assertTrue(ret);
    }
}