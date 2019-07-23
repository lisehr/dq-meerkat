package dqm.jku.trustkg.connectors;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.records.Record;

/**
 * Connector for CSV files
 * 
 * @author Bernhard
 */

public class ConnectorPartialCSV extends ConnectorCSV {

	public final int offset;
	public final int noRecords;
	public boolean init;

	public ConnectorPartialCSV(String filename, String seperator, String linebreak, String label, boolean removeQuotes, int offset, int noRecords) {
		super(filename, seperator, linebreak, label, removeQuotes);
		this.offset = offset;
		this.noRecords = noRecords;
		this.init = true;
	}

	@Override
	public Iterator<Record> getRecords(final Concept concept) throws IOException {
		Iterator<Record> r1 = super.getRecords(concept);
		// avoid title line
		if (init) {
			r1.next();
			init = false;
		}
		for (int i = 0; i < offset && r1.hasNext(); i++) {
			r1.next();
		}

		Iterator<Record> it = new Iterator<Record>() {
			Iterator<Record> records = r1;
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < noRecords && records.hasNext();
			}

			@Override
			public Record next() {
				i++;
				return records.next();
			}
		};

		return it;
	}
}
