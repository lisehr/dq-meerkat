package dqm.jku.trustkg.connectors;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

import com.google.gson.Gson;

import dqm.jku.trustkg.dsd.elements.*;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo.DataType;
import dqm.jku.trustkg.util.DataTypeConverter;
import org.json.JSONObject;
import org.json.JSONArray;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import dqm.jku.trustkg.dsd.DSDFactory;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.util.Constants;
import dqm.jku.trustkg.util.Miscellaneous.DBType;
import org.neo4j.driver.types.Relationship;

/**
 * Connector for neo4j databases
 *
 * @author Katharina Wolf
 */

public class ConnectorNeo4J extends DSConnector {

	@SuppressWarnings("unused")
	private final String DBUrl;
	@SuppressWarnings("unused")
	private final String DBName;
	private final String label;

	private final Driver driver;
	private final Session session;

	Gson gson = new Gson();

	private final static String cypher_schema = "CALL apoc.meta.schema() YIELD value as schemaMap";
	private final static String cypher_get_nr_edges = "MATCH ()-->() RETURN count(*)";
	private final static String cypher_get_nr_nodes = "MATCH (n) RETURN count(n)";
	private final static String cypher_get_relationship_by_type = "MATCH p=()-[r:?]->() RETURN p";
	private final static String cypher_get_nodes_by_type = "MATCH (n:?) RETURN n";
	private final static String cypher_get_unconnected_nodes = "MATCH (n) WHERE NOT (n)--() RETURN n";
	private final static String cypher_get_degree_distribution = "MATCH (n:?)-[]-() RETURN n, count(*)";


	private ConnectorNeo4J(String DBUrl, String DBName, String DBpw, String label) {
		this.DBUrl = DBUrl;
		this.DBName = DBName;
		this.label = label;
		this.driver = GraphDatabase.driver(DBUrl, AuthTokens.basic(DBName, DBpw));
		this.session = driver.session();
	}

	public static ConnectorNeo4J getInstance(String DBUrl, String DBname, String DBpw, String label) {
		ConnectorNeo4J instance;
		instance = new ConnectorNeo4J(DBUrl, DBname, DBpw, label);

		return instance;
	}

	public void close() {
		session.close();
		driver.close();
	}

	@Override
	public Iterator<dqm.jku.trustkg.dsd.records.Record> getRecords(Concept concept) {
		RecordList result = new RecordList();
		try {
			result = getAllNodes(concept);
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}

		return result.iterator();
	}

	@Override
	public RecordList getRecordList(Concept concept) {
		Iterator<dqm.jku.trustkg.dsd.records.Record> iter = getRecords(concept);
		RecordList rs = new RecordList();
		while (iter.hasNext()) {
			rs.addRecord(iter.next());
		}
		return rs;
	}

	@Override
	public RecordList getPartialRecordList(Concept concept, int offset, int noRecords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNrRecords(Concept c) {
		return  getNrNodes();
	}

	public int getNrRelationships(Concept c) {
		return getNrRelationships();
	}

	public boolean isGraphConnected(Concept c) {
		return isConnected();
	}

	public RecordList getDegreeDistribution(Concept c) {
		return getNodeDegree(c);
	}

	@Override
	public Datasource loadSchema() {
		return loadSchema(Constants.DEFAULT_URI, Constants.DEFAULT_PREFIX);
	}

	@Override
	public Datasource loadSchema(String uri, String prefix) {
		Datasource ds = DSDFactory.makeDatasource(label, DBType.NEO4J, uri, prefix);

		Result relSchema = execute(cypher_schema);

		String json = "";

		while (relSchema.hasNext()) {
			Record rec = relSchema.next();
			json = gson.toJson(rec.asMap());
		}

		try {
			JSONObject jo = new JSONObject(json);
			JSONObject schemaObjects = jo.getJSONObject("schemaMap");
			Set<String> keys = schemaObjects.keySet();
			JSONArray schema = schemaObjects.toJSONArray(new JSONArray(keys));

			int counter = 0;

			for(String s : keys) {
				Concept c = DSDFactory.makeConcept(s, ds);
				loadAttributes(c, schema.getJSONObject(counter));
				counter++;
			}
		} catch (Exception e) {
			throw new IllegalStateException("Invalid JSON: " + json, e);
		}

		return ds;
	}


	// ------------------------------------------------------------------------------------------------
	// ------------------------- Private Helping Methods ----------------------------------------------
	// ------------------------------------------------------------------------------------------------

	private void loadAttributes(Concept c, JSONObject obj) {

		// relationships, count, type, properties, labels
		Set<String> keys = obj.keySet();
		Attribute dsdAttribute;
		ReferenceAssociation referenceAttribute;

		for(String s : keys) {

			if(obj.get(s) instanceof JSONObject) {
				JSONObject att = obj.getJSONObject(s);

				// acted_in, reviewed, wrote, produced, directed
				Set<String> attributes = att.keySet();

				for(String a : attributes) {
					dsdAttribute = DSDFactory.makeAttribute(a, c);
					dsdAttribute.setNullable(true);

					try {
						DataTypeConverter.getTypeFromNeo4J(dsdAttribute, "String", a);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			} else {
				Object o = obj.get(s);
				String dataType;

				if(o instanceof Integer) {
					dataType = "int";
				} else if (o instanceof String) {
					dataType = "String";
				} else if (o instanceof Boolean) {
					dataType = "boolean";
				} else {
					dataType = "String";
				}

				if(s.equals("type")) {
					referenceAttribute = DSDFactory.makeReferenceAssociation("Ref/" + c.getLabelOriginal(), c.getDatasource());
					referenceAttribute.setNeo4JType(o.toString());
				} else if (s.equals("count")) {
					referenceAttribute = DSDFactory.makeReferenceAssociation("Ref/" + c.getLabelOriginal(), c.getDatasource());
					referenceAttribute.setNeo4JCount(Integer.parseInt(o.toString()));
				} else {
					dsdAttribute = DSDFactory.makeAttribute(s, c);
					try {
						DataTypeConverter.getTypeFromNeo4J(dsdAttribute, dataType, o.toString());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private RecordList getAllNodes(Concept concept) throws SecurityException, IllegalArgumentException {

		RecordList list = new RecordList();

		// Distinguish between Relationship and Node
		//Attribute type = concept.getAttribute("type");
		String type = concept.getDatasource().getReferenceAssociations("Ref/" + concept.getLabel()).getNeo4JType();

		if(type.equals("relationship")) {
			String query = cypher_get_relationship_by_type;
			query = query.replaceFirst("\\?", concept.getLabelOriginal());
			Result result = execute(query);
			List<Record> records = result.list();

			for(Record r : records) {
				dqm.jku.trustkg.dsd.records.Record rec = new dqm.jku.trustkg.dsd.records.Record(concept);

				for (Relationship item : r.values().get(0).asPath().relationships()) {
					Map<String, Object> map = item.asMap();
					for (Map.Entry<String, Object> entry : map.entrySet()) {
						Attribute a = concept.getAttribute(entry.getKey());
						rec.addValueNeo4J(a, DataTypeConverter.getNeo4JRecordvalue(a, entry.getValue()));
					}
				}
				list.addRecord(rec);
			}
		} else if(type.equals("node")) {
			String query = cypher_get_nodes_by_type;
			query = query.replaceFirst("\\?", concept.getLabelOriginal());
			Result result = execute(query);
			List<Record> records = result.list();

			for(Record r : records) {
				dqm.jku.trustkg.dsd.records.Record rec = new dqm.jku.trustkg.dsd.records.Record(concept);
				Map<String, Object> o = r.values().get(0).asNode().asMap();

				for(Map.Entry<String, Object> entry : o.entrySet()) {
					Attribute a = concept.getAttribute(entry.getKey());
					rec.addValueNeo4J(a, DataTypeConverter.getNeo4JRecordvalue(a, entry.getValue()));
				}
				list.addRecord(rec);
			}
		} else {
			throw new IllegalArgumentException("No mapping known for this Neo4J data type: " + type);
		}
		return list;
	}

	private int getNrRelationships() {
		Result resultRelationships = execute(cypher_get_nr_edges);
		List<Record> records = resultRelationships.list();
		String s = records.get(0).get(0).toString();

		return Integer.parseInt(s);
	}

	private int getNrNodes() {
		Result resultNodes = execute(cypher_get_nr_nodes);
		List<Record> records = resultNodes.list();
		String s =  records.get(0).get(0).toString();

		return Integer.parseInt(s);
	}

	private boolean isConnected() {
		Result resultNodes = execute(cypher_get_unconnected_nodes);
		List<Record> records = resultNodes.list();

		return records.isEmpty();
	}

	private RecordList getNodeDegree(Concept c) {

		String type = c.getDatasource().getReferenceAssociations("Ref/" + c.getLabel()).getNeo4JType();
		if(type.equals("relationship")) {
			return null;
		}

		String query = cypher_get_degree_distribution;
		query = query.replaceFirst("\\?", c.getLabelOriginal());
		Result result = execute(query);
		List<Record> records = result.list();
		RecordList list = new RecordList();

		for(Record r : records) {
			Map<String, Object> o = r.values().get(0).asNode().asMap();
			Object counter = r.values().get(1);

			c.addAttribute(DSDFactory.makeAttribute("NodeDegree", c));
			Attribute nodeDegree = c.getAttribute("NodeDegree");
			nodeDegree.setDataType(Integer.class);
			nodeDegree.setNodeDegree(Integer.parseInt(counter.toString()));

			dqm.jku.trustkg.dsd.records.Record rec = new dqm.jku.trustkg.dsd.records.Record(c);
			rec.addValueNeo4J(nodeDegree, Integer.parseInt(counter.toString()));

			for(Map.Entry<String, Object> entry : o.entrySet()) {
				Attribute a = c.getAttribute(entry.getKey());
				rec.addValueNeo4J(a, DataTypeConverter.getNeo4JRecordvalue(a, entry.getValue()));
			}

			list.addRecord(rec);
		}
		return list;
	}

	private Result execute(String statement) {
		return session.run(statement);
	}

	private String getLabelString(Object o) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		ArrayList<?> labelsGeneric;
		ArrayList<String> labels = new ArrayList<>();

		Field field = o.getClass().getDeclaredField("labels");
		field.setAccessible(true);
		labelsGeneric = (ArrayList<?>) field.get(o);

		for (Object value : labelsGeneric) {
			labels.add(value.toString());
		}
		return labels.toString();
	}

	private HashMap<String, String> getPropertiesMap(Object o) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HashMap<?, ?> propertiesGeneric;
		HashMap<String, String> properties = new HashMap<>();

		Field field = o.getClass().getSuperclass().getDeclaredField("properties");
		field.setAccessible(true);
		propertiesGeneric = (HashMap<?, ?>) field.get(o);

		for(Map.Entry<?, ?> entry : propertiesGeneric.entrySet()) {
			Object key = entry.getKey();
			Object val = entry.getValue();

			properties.put(key.toString(), val.toString());
		}
		return properties;
	}
}


