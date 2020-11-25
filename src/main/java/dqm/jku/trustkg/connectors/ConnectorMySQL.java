package dqm.jku.trustkg.connectors;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dqm.jku.trustkg.dsd.DSDFactory;
import dqm.jku.trustkg.dsd.elements.AggregationAssociation;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.elements.ForeignKey;
import dqm.jku.trustkg.dsd.elements.InheritanceAssociation;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.util.AttributeSet;
import dqm.jku.trustkg.util.Constants;
import dqm.jku.trustkg.util.Miscellaneous.DBType;
import dqm.jku.trustkg.util.converters.DataTypeConverter;

/**
 * Connector for relational MySQL DBs
 * 
 * @author Lisa & Bernhard
 */

public class ConnectorMySQL extends DSConnector {

	private final Connection connection;
	@SuppressWarnings("unused")
	private final String DBUrl;
	private final String DBName;

	private final Map<String, String> inheritanceAssociations = new HashMap<String, String>();
	private final Map<String, List<String>> aggregationAssociation = new HashMap<String, List<String>>();
	private final Set<String> referenceAssociation = new HashSet<String>();

	private static HashMap<String, ConnectorMySQL> instances = new HashMap<String, ConnectorMySQL>();

	private final static String sql_get_tables = "SELECT table_name FROM information_schema.tables WHERE table_schema = ? " + " AND table_type = 'BASE TABLE'";

	private final static String sql_get_cols_to_table = "SELECT column_name, is_nullable, extra, ordinal_position, column_default, "
			+ " data_type, column_key FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";

	private final static String sql_get_foreignkeys = "SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, "
			+ "REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME FROM information_schema.KEY_COLUMN_USAGE where constraint_schema = ? "
			+ "and constraint_name != 'PRIMARY' and Referenced_table_schema IS NOT NULL ORDER BY ORDINAL_POSITION";

	private final static String sql_get_foreignkey_rules = "SELECT UPDATE_RULE, DELETE_RULE FROM INformation_schema.REFERENTIAL_CONSTRAINTS " + " WHERE CONSTRAINT_SCHEMA = ? AND CONSTRAINT_NAME = ?";

	private final static String sql_get_concept_records = "SELECT * FROM ? LIMIT ? OFFSET ? ";

	private final static String sql_get_concept_records_count = "SELECT COUNT(*) AS SIZE FROM ";

	public static ConnectorMySQL getInstance(String DBUrl, String DBName, String DBuser, String DBpw) {
		if (instances.containsKey(DBUrl + DBName))
			return instances.get(DBUrl + DBName);

		ConnectorMySQL instance;
		try {
			instance = new ConnectorMySQL(DBUrl, DBName, DBuser, DBpw);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
		instances.put(DBUrl, instance);
		return instance;
	}

	private ConnectorMySQL(String DBUrl, String DBName, String DBuser, String DBpw) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(DBUrl + "?user=" + DBuser + "&password=" + DBpw + "&verifyServerCertificate=true&useSSL=false&requireSSL=false&serverTimezone=UTC");
		this.DBUrl = DBUrl;
		this.DBName = DBName;
	}

	@Override
	public Iterator<Record> getRecords(final Concept concept) throws IOException {

		final int size = getNrRecords(concept);

		return new Iterator<Record>() {
			private int count = 0;
			private static final int buffersize = 1000;
			private Record[] buffer = new Record[buffersize];

			@Override
			public boolean hasNext() {
				return count < size;
			}

			@Override
			public Record next() {
				if (count % buffersize == 0) {
					try {
						fillBuffer();
					} catch (SQLException | ParseException e) {
						e.printStackTrace();
					}
				}
				return buffer[count++ % buffersize];
			}

			private void fillBuffer() throws SQLException, ParseException {
				String query = sql_get_concept_records;
				query = query.replaceFirst("\\?", DBName + "." + concept.getLabel());

				PreparedStatement stmt = connection.prepareStatement(query);
				stmt.setInt(1, buffersize);
				stmt.setInt(2, count);

				ResultSet rs = stmt.executeQuery();
				int i = 0;
				while (rs.next()) {
					// build Record
					Record r = new Record(concept);
					for (Attribute a : concept.getAttributes()) {
						r.addValue(a, DataTypeConverter.getMySQLRecordvalue(a, rs));
					}
					buffer[i++] = r;
				}

			}

		};
	}

	public void findFunctionalDependencies(Concept concept) throws IOException {
		AttributeSet primarKey = concept.getPrimaryKeys();
		Set<AttributeSet> lefts = new HashSet<AttributeSet>();

		for (Attribute a : concept.getAttributes()) {
			if (primarKey.getSize() > 0)
				DSDFactory.makeFunctionalDependency(primarKey, a, concept);
			lefts.add(new AttributeSet(a));
		}
		FDAnalyzer analyzer = new FDAnalyzer(lefts, concept.getAttributes());
		analyzer.analyze(getRecords(concept), concept);
	}

	@Override
	public Datasource loadSchema(String uri, String prefix) throws IOException {
		Datasource ds = DSDFactory.makeDatasource(DBName, DBType.MYSQL, uri, prefix);

		try {
			PreparedStatement getConcepts = connection.prepareStatement(sql_get_tables);
			getConcepts.setString(1, DBName);

			ResultSet rs = getConcepts.executeQuery();

			while (rs.next()) {
				String tname = getName(rs, "TABLE_NAME");
				Concept c = createConcept(tname, ds);
				loadAttributes(c);
			}
			getForeignKeys(ds);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		setAssociationDirections(ds);

		return ds;
	}
	
	@Override
	public Datasource loadSchema() throws IOException {
		return loadSchema(Constants.DEFAULT_URI, Constants.DEFAULT_PREFIX);
	}

	private void setAssociationDirections(Datasource ds) {
		List<Concept> inhConcepts = DSDElement.getAllConcepts().stream().filter(x -> inheritanceAssociations.containsKey(x.getLabel())).collect(Collectors.toList());
		inhConcepts.forEach(x -> ((InheritanceAssociation) x).setParent(ds.getConcept(inheritanceAssociations.get(x.getLabel()))));

		List<Concept> aggConcepts = DSDElement.getAllConcepts().stream().filter(x -> aggregationAssociation.containsKey(x.getLabel())).collect(Collectors.toList());
		for (Concept c : aggConcepts) {
			AggregationAssociation assoc = (AggregationAssociation) c;
			Set<ForeignKey> fkList = assoc.getForeignKeys();
			List<Attribute> fkAttributes = aggregationAssociation.get(assoc.getLabel()).stream().map(x -> assoc.getAttribute(x)).collect(Collectors.toList());
			AttributeSet fkAttSet = new AttributeSet(fkAttributes);

			for (ForeignKey fk : fkList) {
				AttributeSet otherFkAttSet = new AttributeSet(fk.getReferencingAttributes());
				if (fkAttSet.equals(otherFkAttSet)) {
					assoc.setAggregate(fk);
					break;
				}
			}
		}
	}

	private Concept createConcept(String tname, Datasource ds) {
		Concept c;
		if (referenceAssociation.contains(tname)) {
			c = DSDFactory.makeReferenceAssociation(tname, ds);
		} else if (inheritanceAssociations.containsKey(tname)) {
			c = DSDFactory.makeInheritanceAssociation(tname, ds);
		} else if (aggregationAssociation.containsKey(tname)) {
			c = DSDFactory.makeAggregationAssociation(tname, ds);
		} else {
			c = DSDFactory.makeConcept(tname, ds);
		}

		return c;
	}

	private void loadAttributes(Concept c) {
		try {
			PreparedStatement getConceptDescription = connection.prepareStatement(sql_get_cols_to_table);
			getConceptDescription.setString(1, DBName);
			getConceptDescription.setString(2, c.getLabel());

			ResultSet rs = getConceptDescription.executeQuery();

			while (rs.next()) {
				Attribute a = DSDFactory.makeAttribute(getName(rs, "COLUMN_NAME"), c);
				a.setAutoIncrement(rs.getString("EXTRA").contains("auto_increment"));
				a.setNullable(rs.getString("IS_NULLABLE").equals("NO") ? false : true);
				a.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));

				try {
					DataTypeConverter.getTypeFromMySQL(a, getName(rs, "DATA_TYPE"), rs.getString("COLUMN_DEFAULT"));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				String key = rs.getString("COLUMN_KEY");
				switch (key) {
				case "PRI":
					a.setUnique(true);
					c.addPrimaryKeyAttribute(a);
					break;
				case "UNI":
					a.setUnique(true);
					break;
				case "MUL":
				default:
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void getForeignKeys(Datasource ds) throws IOException {
		try {
			PreparedStatement getFK = connection.prepareStatement(sql_get_foreignkeys);
			getFK.setString(1, DBName);
			ResultSet rs = getFK.executeQuery();

			while (rs.next()) {
				Concept fromC = createConcept(getName(rs, "TABLE_NAME"), ds);
				Concept toC = createConcept(getName(rs, "REFERENCED_TABLE_NAME"), ds);
				ForeignKey fk = DSDFactory.makeForeignKey(getName(rs, "CONSTRAINT_NAME"), fromC, toC);
				fk.addAttributePair(DSDFactory.makeAttribute(getName(rs, "COLUMN_NAME"), fromC), DSDFactory.makeAttribute(getName(rs, "REFERENCED_COLUMN_NAME"), toC));

				PreparedStatement getFKRules = connection.prepareStatement(sql_get_foreignkey_rules);
				getFKRules.setString(1, DBName);
				getFKRules.setString(2, fk.getLabel());
				ResultSet rs1 = getFKRules.executeQuery();

				if (rs1.next()) {
					fk.setUpdateRule(DataTypeConverter.getFKRuleFromString(rs1.getString("UPDATE_RULE")));
					fk.setDeleteRule(DataTypeConverter.getFKRuleFromString(rs1.getString("DELETE_RULE")));
				}
				if (rs1.next()) {
					throw new IOException("Duplicate entry for foreign key " + fk.getLabel());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String getName(ResultSet rs, String s) throws SQLException {
		return rs.getString(s).toLowerCase();
	}

	@Override
	public int getNrRecords(Concept c) throws IOException {
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement(sql_get_concept_records_count + DBName + "." + c.getLabel());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				return rs.getInt("SIZE");
			}
			return 0;
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}
	}

	public void defineInheritanceAssociation(String assocName, String parentName) {
		inheritanceAssociations.put(assocName, parentName);
	}

	/**
	 * @param assocName
	 * @param attributeList
	 *            list of attribute names that define the foreign keys towards the
	 *            aggregate
	 */
	public void defineAggregationAssociation(String assocName, List<String> attributeList) {
		aggregationAssociation.put(assocName, attributeList);
	}

	public void defineReferenceAssociation(String assocName) {
		referenceAssociation.add(assocName);
	}

	@Override
	public RecordList getRecordList(Concept concept) throws IOException {
		Iterator<Record> rIt = getRecords(concept);
		RecordList rs = new RecordList();
		while (rIt.hasNext()) {
			rs.addRecord(rIt.next());
		}
		return rs;
	}

	@Override
	public RecordList getPartialRecordList(Concept concept, int offset, int noRecs) throws IOException {
		Iterator<Record> rIt = getRecords(concept);
		RecordList rs = new RecordList();
		int i = 0;
		while (rIt.hasNext() && i < offset) {
			rIt.next();
			i++;
		}
		i = 0;
		while (rIt.hasNext() && i < noRecs) {
			rs.addRecord(rIt.next());
			i++;
		}
		return rs;
	}
}
