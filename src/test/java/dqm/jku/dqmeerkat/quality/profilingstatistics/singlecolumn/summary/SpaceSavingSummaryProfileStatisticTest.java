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
    void getValue() {
        // given

        // when

        // then
        Assertions.fail("Not implemented");
    }

    @Test
    void getValueClass() {
        // given

        // when

        // then
        Assertions.fail("Not implemented");
    }

    @Test
    void calculationNumeric() {
        // given

        // when

        // then
        Assertions.fail("Not implemented");
    }

    @Test
    void checkConformance() {
        // given

        // when

        // then
        Assertions.fail("Not implemented");
    }
}