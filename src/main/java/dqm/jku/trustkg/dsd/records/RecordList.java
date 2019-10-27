package dqm.jku.trustkg.dsd.records;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.list.TreeList;

public class RecordList implements Iterable<Record> {

  @SuppressWarnings("unchecked")
  private List<Record> records = new TreeList();

  public void addRecord(Record r) {
    records.add(r);
  }

  public int size() {
    return records.size();
  }

  @Override
  public Iterator<Record> iterator() {
    return records.iterator();
  }

  public boolean contains(Record r) {
    return records.contains(r);
  }
}
