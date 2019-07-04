package connectors;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dsd.DSDFactory;
import dsd.elements.Attribute;
import dsd.elements.Concept;
import dsd.elements.Datasource;
import dsd.records.Record;
import util.AttributeSet;

/**
 * Connector for CSV files
 * 
 * @author Bernhard
 */

public class ConnectorCSV extends DSInstanceConnector {

	public final String filename;
	public final String label;
	public final String seperator;
	public final String linebreak;
	public final boolean removeQuotes;
	private final File file;
	public static final int bufferSize = 1000;

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
			boolean closed = false;
			boolean init = true;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;

			@Override
			public boolean hasNext() {
				// exclude title line
				if (init) {
					init = false;
					try {
						reader.readLine();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
				if (closed)
					return false;
				if (line != null)
					return true;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return line != null;
			}

			@Override
			public Record next() {
				if (line == null)
					try {
						line = reader.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				String[] values = removeQuotes ? line.replace("\"", "").split(seperator) : line.split(seperator);
				Record r = new Record(concept);
				for (int j = 0; j < Math.min(values.length, attributes.size()); j++)
					r.addValue(attributes.get(j), values[j]);
				line = null;
				return r;
			}

		};
	}

	@Override
	public Datasource loadSchema() throws IOException {
		Datasource ds = DSDFactory.makeDatasource(label);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String[] attNames = reader.readLine().split(seperator);
		Concept c = DSDFactory.makeConcept(label, ds);
		int i = 0;
		for (String s : attNames) {
			if (removeQuotes)
				s = s.replace("\"", "");
			Attribute a = DSDFactory.makeAttribute(s, c);
			a.setDataType(String.class);
			a.setOrdinalPosition(i++);
			a.setNullable(true);
			a.setAutoIncrement(false);
		}
		reader.close();
		return ds;
	}

	@Override
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
