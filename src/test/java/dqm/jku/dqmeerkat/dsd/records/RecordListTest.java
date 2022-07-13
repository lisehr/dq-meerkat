package dqm.jku.dqmeerkat.dsd.records;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordListTest {

    @Test
    public void createRecordListFromListTest() {
        // given
        List<Number> data = List.of(1, 2, 3, 4, 5);
        var attributeName = "test";

        // when
        var recordList = new RecordList(data, attributeName);

        // then
        assertEquals(5, recordList.size());
        assertEquals(attributeName, recordList.getAttributes().get(attributeName).getLabel());
    }

}