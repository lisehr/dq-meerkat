package dqm.jku.trustkg.dsd.records;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RecordSet implements Iterable<Record> {

  private Set<Record> records = new HashSet<Record>();

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
