package dqm.jku.trustkg.dsd.records;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class RecordSet implements Iterable<Record> {
	
	private SortedSet<Record> records = new TreeSet<Record>();
	
	public void addRecord(Record r) {
		records.add(r);
	}

	@Override
	public Iterator<Record> iterator() {
		return records.iterator();
	}
	
	public boolean contains(Record r) {
		return records.contains(r);
	}

}
