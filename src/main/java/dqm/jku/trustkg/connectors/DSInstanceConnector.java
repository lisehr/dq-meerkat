package dqm.jku.trustkg.connectors;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;

public abstract class DSInstanceConnector extends DSConnector {

	public abstract Iterator<Record> getRecords(Concept concept) throws IOException;
	
	public abstract RecordSet getRecordSet(Concept concept) throws IOException;

	public abstract void findFunctionalDependencies(Concept concept) throws IOException;

	public abstract int getNrRecords(Concept c) throws IOException;

	public Iterable<Record> records(final Concept c) {
		return new Iterable<Record>() {

			@Override
			public Iterator<Record> iterator() {
				try {
					return getRecords(c);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}

}
