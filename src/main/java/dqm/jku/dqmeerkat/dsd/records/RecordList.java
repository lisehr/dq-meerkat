package dqm.jku.dqmeerkat.dsd.records;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.util.AttributeSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


public class RecordList implements Iterable<Record> {

    private List<Record> records = new ArrayList<>();

    public RecordList() {

    }


    public RecordList(List<Record> recs) {
        records.addAll(recs);
    }


    /**
     * <p>
     * DIRTY HACK to get a recordlist without too much of a hassle. Note that the url of the attribute
     * is prefixed with "null/"
     * </p>
     *
     * @param medians
     * @param attributeName Dummyname, necessary because of type erasure
     */
    public RecordList(List<Number> medians, String attributeName) {
        var concept = new Concept();
        var attribute = new Attribute(attributeName, concept);
        attribute.setDataType(double.class);
        concept.addAttribute(attribute);
        attribute.setURI(attributeName);
        records = medians.stream().map(number -> {
            var record = new Record(concept);
            record.addValue(attribute, number);
            return record;
        }).collect(Collectors.toList());

    }

    public boolean addRecord(Record r) {
        return records.add(r);
    }

    public boolean addAll(RecordList rl) {
        return records.addAll(rl.toList());
    }

    public List<Record> toList() {
        return records;
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    public int size() {
        return records.size();
    }

    public RecordList splitPartialRecordList(int offset, int noRecs) {
        if (offset < 0 || noRecs < 0 || (offset + noRecs) > size()) {
            return new RecordList();
        }
        int end = noRecs > size() ? size() : (noRecs + offset);
        return new RecordList(records.subList(offset, end));
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }

    public boolean contains(Record r) {
        return records.contains(r);
    }

    public AttributeSet getAttributes() {
        if (this.isEmpty()) return null;
        return this.records.get(0).getFields();
    }

    public RecordList getValues(Concept c, String label) {
        RecordList rl = new RecordList();

        for (Record r : records) {
            Attribute a = r.getFields().get(label);
            Object o = r.getValue(a);
            if (o != null) {
                Record rec = new Record(c);
                rec.addValueNeo4J(a, o);
                rl.addRecord(rec);
            }
        }
        return rl;
    }
}

