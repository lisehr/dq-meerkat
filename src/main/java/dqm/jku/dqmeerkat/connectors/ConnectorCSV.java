package dqm.jku.dqmeerkat.connectors;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dqm.jku.dqmeerkat.dsd.DSDFactory;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.util.AttributeSet;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.Miscellaneous.DBType;
import dqm.jku.dqmeerkat.util.converters.DataTypeConverter;

/**
 * Connector for CSV files
 * 
 * @author Bernhard
 */

public class ConnectorCSV extends DSConnector {

	public final String filename;
	public final String label;
	public final String seperator;
	public final String linebreak;
	public final boolean removeQuotes;
	private final File file;
	private final Map<Concept, RecordList> recordMap = new HashMap<>();

	public ConnectorCSV(String filename, String seperator, String linebreak) {
		this(filename, seperator, linebreak, filename);
	}

	public ConnectorCSV(String filename, String seperator, String linebreak, String label) {
		this(filename, seperator, linebreak, label, false);
	}

	public ConnectorCSV(String filename, String seperator, String linebreak, String label, boolean removeQuotes) {
		super();
		this.filename = filename;
		this.seperator = seperator;
		this.linebreak = linebreak;
		this.label = label;
		this.removeQuotes = removeQuotes;
		file = new File(filename);
	}

	@Override
	public Iterator<Record> getRecords(final Concept concept) throws IOException {

		return new Iterator<Record>() {
			List<Attribute> attributes = concept.getSortedAttributes();	
			boolean init = true;
			boolean first = true;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;

			@Override
			public boolean hasNext() {
				// Exclude title line when initially getting records from this concept
				if (init) {	
					init = false;
					try {
						reader.readLine();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
				// Return true if line has been already read
				if (line != null) return true;
				// Otherwise: try to read new line and store if for .next() calls
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return line != null;
			}

			@Override
			public Record next() {
				// Read new line if no line available from .hasNext()
				if (line == null) {
					try {
						line = reader.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				String[] values = removeQuotes ? line.replace("\"", "").split(seperator) : line.split(seperator);

				// Change data types for CSV files (default: Object) when reading field values for the first time
				if (first) {
					for (int j = 0; j < Math.min(values.length, attributes.size()); j++) {
						DataTypeConverter.getDataTypeFromCSVRecord(attributes.get(j), values[j]);
					}
					first = false;
				}
				Record r = new Record(concept);
				for (int j = 0; j < Math.min(values.length, attributes.size()); j++) {
					r.addValueFromCSV(attributes.get(j), values[j]);
				}
				line = null;
				return r;
			}
		};
	}

	public RecordList getRecordList(final Concept concept) throws IOException {
	  if (!recordMap.values().isEmpty()) return recordMap.get(concept);
		Iterator<Record> rIt = getRecords(concept);
		RecordList rs = new RecordList();
		while (rIt.hasNext()) {
			rs.addRecord(rIt.next());
		}
		recordMap.put(concept, rs);
		return rs;
	}

	@Override
	public RecordList getPartialRecordList(Concept concept, int offset, int noRecs) throws IOException {
	  if (!recordMap.values().isEmpty()) return recordMap.get(concept).splitPartialRecordList(offset, noRecs);
    RecordList allRecords = getRecordList(concept);
    return allRecords.splitPartialRecordList(offset, noRecs);
	}

	@Override
	public Datasource loadSchema() throws IOException {
		return loadSchema(Constants.DEFAULT_URI, Constants.DEFAULT_PREFIX);
	}
	
	@Override
	public Datasource loadSchema(String uri, String prefix) throws IOException {
		Datasource ds = DSDFactory.makeDatasource(label, DBType.CSV, uri, prefix);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String[] attNames = reader.readLine().split(seperator);
		Concept c = DSDFactory.makeConcept(label, ds);
		int i = 0;
		for (String s : attNames) {
			if (removeQuotes) s = s.replace("\"", "");
			Attribute a = DSDFactory.makeAttribute(s, c);
			a.setDataType(String.class);
			a.setOrdinalPosition(i++);
			a.setNullable(true);
			a.setAutoIncrement(false);
		}
		reader.close();
		return ds;
	}

	public void findFunctionalDependencies(Concept concept) throws IOException {
		FDAnalyzer analyzer = new FDAnalyzer(getFDLeftSides(concept), concept.getAttributes());
		analyzer.analyze(getRecords(concept), concept);
	}

	private Set<AttributeSet> getFDLeftSides(Concept concept) {
		// TODO extend to powerset + intelligence
		Set<AttributeSet> sets = new HashSet<AttributeSet>();
		for (Attribute a : concept.getAttributes()) {
			sets.add(new AttributeSet(a));
		}
		return sets;
	}

	@Override
	public int getNrRecords(Concept concept) throws IOException {
		try (InputStream is = new BufferedInputStream(new FileInputStream(filename));) {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		}
	}

}