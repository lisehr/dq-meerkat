package dqm.jku.dqmeerkat.connectors;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;

public abstract class DSConnector {

	public abstract Datasource loadSchema() throws IOException;

	public abstract Datasource loadSchema(String uri, String prefix) throws IOException;

	public abstract Iterator<Record> getRecords(Concept concept) throws IOException;

	public abstract int getNrRecords(Concept c) throws IOException;

	public abstract RecordList getRecordList(Concept concept) throws IOException;

	public abstract RecordList getPartialRecordList(Concept concept, int offset, int noRecords) throws IOException;

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
