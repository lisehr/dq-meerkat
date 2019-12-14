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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.AttributeList;

import dqm.jku.trustkg.dsd.DSDFactory;
import dqm.jku.trustkg.dsd.elements.AggregationAssociation;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.elements.ForeignKey;
import dqm.jku.trustkg.dsd.elements.InheritanceAssociation;
import dqm.jku.trustkg.util.AttributeSet;
import dqm.jku.trustkg.util.DataTypeConverter;
import dqm.jku.trustkg.util.Miscellaneous.DBType;

/**
 * Connector for relational Oracle DBs
 * 
 * @author Elisa
 */

public class ConnectorOracle {
	private final Connection connection;
	@SuppressWarnings("unused")
	private final String DBUrl;
	private final String DBName;

	private final Map<String, String> inheritanceAssociations = new HashMap<String, String>();
	private final Map<String, List<String>> aggregationAssociation = new HashMap<String, List<String>>();
	private final Set<String> referenceAssociation = new HashSet<String>();

	private static HashMap<String, ConnectorOracle> instances = new HashMap<String, ConnectorOracle>();

	private final static String sql_get_tables = "SELECT table_name FROM user_tables";

	private final static String sql_get_cols_to_table = "SELECT utc.column_name, data_type, nullable, data_default, column_id, constraint_type " + "FROM user_tab_columns utc "
			+ "LEFT OUTER JOIN user_cons_columns ucc ON utc.table_name = ucc.table_name AND utc.column_name = ucc.column_name "
			+ "LEFT OUTER JOIN user_constraints uc ON uc.constraint_name = ucc.constraint_name " + "WHERE lower(utc.table_name) = ? ORDER BY column_id";

	private final static String sql_get_foreignkeys = "SELECT ucc.constraint_name, ucc.table_name, ucc.column_name, uc_pk.table_name ref_table_name, ucc_pk.column_name ref_column_name "
			+ "FROM user_cons_columns ucc JOIN user_constraints uc ON ucc.owner = uc.owner  AND ucc.constraint_name = uc.constraint_name "
			+ "JOIN user_constraints uc_pk ON uc.r_owner = uc_pk.owner AND uc.r_constraint_name = uc_pk.constraint_name "
			+ "JOIN user_cons_columns ucc_pk ON uc_pk.table_name = ucc_pk.table_name AND uc_pk.constraint_name = ucc_pk.constraint_name " + "WHERE uc.constraint_type = 'R'";

	private final static String sql_get_foreignkey_rules = "SELECT delete_rule FROM user_constraints WHERE lower(constraint_name) = ?";

	public static ConnectorOracle getInstance(String DBUrl, String DBName, String DBuser, String DBpw) {
		if (instances.containsKey(DBUrl + DBName))
			return instances.get(DBUrl + DBName);

		ConnectorOracle instance;

		try {
			instance = new ConnectorOracle(DBUrl, DBName, DBuser, DBpw);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.out.println("connection failed");
			return null;
		}

		instances.put(DBUrl, instance);
		return instance;
	}

	private ConnectorOracle(String DBUrl, String DBName, String DBUser, String DBpw) throws ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		this.connection = DriverManager.getConnection(DBUrl, DBUser, DBpw);

		this.DBUrl = DBUrl;
		this.DBName = DBName;
	}

	public Datasource loadSchema() throws IOException {
		Datasource ds = DSDFactory.makeDatasource(DBName, DBType.ORACLE);

		try {
			PreparedStatement getConcepts = connection.prepareStatement(sql_get_tables);

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

	private String getName(ResultSet rs, String s) throws SQLException {
		return rs.getString(s).toLowerCase();
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
			getConceptDescription.setString(1, c.getLabel());

			ResultSet rs = getConceptDescription.executeQuery();

			while (rs.next()) {
				Attribute a = DSDFactory.makeAttribute(getName(rs, "COLUMN_NAME"), c);
				a.setNullable(rs.getString("NULLABLE").equals("N") ? false : true);
				a.setOrdinalPosition(rs.getInt("COLUMN_ID"));

				try {
					DataTypeConverter.getTypeFromOracleSQL(a, getName(rs, "DATA_TYPE"), rs.getString("DATA_DEFAULT"));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				String key = rs.getString("CONSTRAINT_TYPE");
				if (key != null) {
					switch (key) {
					case "P":
						a.setUnique(true);
						c.addPrimaryKeyAttribute(a);
						break;
					case "U":
						a.setUnique(true);
						break;
					default:
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void getForeignKeys(Datasource ds) throws IOException {
		try {
			PreparedStatement getFK = connection.prepareStatement(sql_get_foreignkeys);
			ResultSet rs = getFK.executeQuery();

			while (rs.next()) {
				Concept fromC = createConcept(getName(rs, "TABLE_NAME"), ds);
				Concept toC = createConcept(getName(rs, "REF_TABLE_NAME"), ds);
				ForeignKey fk = DSDFactory.makeForeignKey(getName(rs, "CONSTRAINT_NAME"), fromC, toC);
				fk.addAttributePair(DSDFactory.makeAttribute(getName(rs, "COLUMN_NAME"), fromC), DSDFactory.makeAttribute(getName(rs, "REF_COLUMN_NAME"), toC));

				PreparedStatement getFKRules = connection.prepareStatement(sql_get_foreignkey_rules);
				getFKRules.setString(1, fk.getLabel());
				ResultSet rs1 = getFKRules.executeQuery();

				if (rs1.next()) {
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

	@SuppressWarnings("static-access")
	private void setAssociationDirections(Datasource ds) {
		List<Concept> inhConcepts = DSDElement.getAllConcepts().stream().filter(x -> inheritanceAssociations.containsKey(x.getLabel())).collect(Collectors.toList());
		inhConcepts.forEach(x -> ((InheritanceAssociation) x).setParent(ds.getConcept(inheritanceAssociations.get(x.getLabel()))));

		List<Concept> aggConcepts = DSDElement.getAllConcepts().stream().filter(x -> aggregationAssociation.containsKey(x.getLabel())).collect(Collectors.toList());
		for (Concept c : aggConcepts) {
			AggregationAssociation assoc = (AggregationAssociation) c;
			Set<ForeignKey> fkList = assoc.getForeignKeys();
			List<Attribute> fkAttributes = aggregationAssociation.get(assoc.getLabel()).stream().map(x -> assoc.getAttribute(x)).collect(Collectors.toList());
			AttributeList fkAttSet = new AttributeList();

			for (ForeignKey fk : fkList) {
				AttributeSet otherFkAttSet = new AttributeSet(fk.getReferencingAttributes());
				if (fkAttSet.equals(otherFkAttSet)) {
					assoc.setAggregate(fk);
					break;
				}
			}
		}
	}

}
