package dqm.jku.trustkg.connectors;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Record;

import dqm.jku.trustkg.dsd.DSDFactory;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.util.Constants;
import dqm.jku.trustkg.util.Miscellaneous.DBType;

/**
 * Connector for neo4j databases
 * 
 * @author Katharina
 */

public class ConnectorNeo4J extends DSConnector {

	@SuppressWarnings("unused")
	private final String DBUrl;
	@SuppressWarnings("unused")
	private final String DBname;
	private final String label;
	
	private Driver driver;
	private Session session;

	private final static String cypher_get_all_labels = "call db.labels()";
	private final static String cypher_get_all_property_keys = "CALL db.propertyKeys()";
	private final static String cypher_get_all_relationship_types = "call db.relationshipTypes()";
	private final static String cypher_get_nr_edges = "MATCH ()-->() RETURN count(*)";
	private final static String cypher_get_nr_nodes = "MATCH (n) RETURN count(n)";
	private final static String cypher_get_all_nodes = "MATCH (n) RETURN n";
	private final static String cypher_get_unconnected_nodes = "MATCH (n) WHERE NOT (n)--() RETURN n";
	private final static String cypher_get_all_nodes_and_relationships = "MATCH (n)-[r]-(p) RETURN n, r, p";
	private final static String cypher_get_degree_distribution = "MATCH (n)-[]-() RETURN n, count(*)";
	
	
	private ConnectorNeo4J(String DBUrl, String DBname, String DBpw, String label) {
		this.DBUrl = DBUrl;
		this.DBname = DBname;
		this.label = label;
		this.driver = GraphDatabase.driver(DBUrl, AuthTokens.basic(DBname, DBpw));
		this.session = driver.session();
	}
	
	public static ConnectorNeo4J getInstance(String DBUrl, String DBname, String DBpw, String label) throws ClassNotFoundException {
		ConnectorNeo4J instance;
		instance = new ConnectorNeo4J(DBUrl, DBname, DBpw, label);
		
		return instance;
	}
	
	public void close() {
		session.close();
		driver.close();
	}
	
	@Override
	public Iterator<dqm.jku.trustkg.dsd.records.Record> getRecords(Concept concept) throws IOException {	
		RecordList result = new RecordList();
		try {
			result = getAllNodes(concept);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	
		return result.iterator();
	}

	@Override
	public RecordList getRecordList(Concept concept) throws IOException {
		Iterator<dqm.jku.trustkg.dsd.records.Record> iter = getRecords(concept);
		RecordList rs = new RecordList();
		while (iter.hasNext()) {
			rs.addRecord(iter.next());
		}
		
		return rs;
	}
	

	@Override
	public RecordList getPartialRecordList(Concept concept, int offset, int noRecords) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param c Concept c
	 * @return number of nodes in neo4j
	 * @throws IOException
	 */
	@Override
	public int getNrRecords(Concept c) throws IOException {
		return  getNrNodes();
	}
	
	/**
	 * 
	 * @param c Concept c
	 * @return number of edges in neo4j
	 * @throws IOException
	 */
	public int getNrRelationships(Concept c) throws IOException {
		return getNrRelationships();
	}
	
	/**
	 * 
	 * @param c Concept c
	 * @return true, if Neo4J Graph is fully connected, else false
	 * @throws IOException
	 */
	public boolean isGraphFullyConnected(Concept c) throws IOException {
		return isFullyConnected();
	}
	
	/**
	 * 
	 * @param c Concept c
	 * @return nodes and the number of incoming + outgoint relationships relationships
	 */
	public Iterator<dqm.jku.trustkg.dsd.records.Record> getDegreeDistribution(Concept c) {
		RecordList rs = new RecordList();
		
		try {
			rs = getNodeDegree(c);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}

		return rs.iterator();
	}

	/**
	 * return returns the labels, properties and relationship-types in neo4j database
	 */
	@Override
	public Datasource loadSchema() throws IOException {
		return loadSchema(Constants.DEFAULT_URI, Constants.DEFAULT_PREFIX);
	}

	/**
	 * @return returns the labels, properties and relationship-types in neo4j database
	 */
	@Override
	public Datasource loadSchema(String uri, String prefix) throws IOException {
		Datasource ds = DSDFactory.makeDatasource(label, DBType.NEO4J, uri, prefix);
		Concept c = DSDFactory.makeConcept(label, ds);
		StatementResult nodelabels = getAllLabels();
		List<Record> records = nodelabels.list();
		int i = 0;
		for(Record r: records) {
			String s = r.get(0).toString();
			Attribute a = DSDFactory.makeAttribute(s, c);
			a.setDataType(String.class);
			a.setOrdinalPosition(i++);
			a.setNullable(true);
			a.setAutoIncrement(false);			
		}
		
		StatementResult nodeProperties = getAllPropertyKeys();
		records = nodeProperties.list();
		for(Record r: records) {
			String s = r.get(0).toString();
			Attribute a = DSDFactory.makeAttribute(s, c);
			a.setDataType(String.class);
			a.setOrdinalPosition(i++);
			a.setNullable(true);
			a.setAutoIncrement(false);			
		}
		
		StatementResult relType = getAllRelationshipTypes();
		records = relType.list();
		for(Record r: records) {
			String s = r.get(0).toString();
			Attribute a = DSDFactory.makeAttribute(s, c);
			a.setDataType(String.class);
			a.setOrdinalPosition(i++);
			a.setNullable(true);
			a.setAutoIncrement(false);			
		}
		
		return ds;
	}
	

	// ------------------------------------------------------------------------------------------------
	// ------------------------- Private Helping Methods ----------------------------------------------
	// ------------------------------------------------------------------------------------------------
	
	private RecordList getAllNodes(Concept concept) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		StatementResult result = execute(cypher_get_all_nodes);
		List<Record> records = result.list();
		RecordList list = new RecordList();
		
		for(Record r : records) {
			String labels = "";
			HashMap<String, String> properties = new HashMap<>();		
			Map<String, Object> map = r.asMap();

			for(Map.Entry<String, Object> entry : map.entrySet()) {
				Object o = entry.getValue();
				
				labels = getLabelString(o);
				properties = getPropertiesMap(o);
			}
			
			Attribute att  = new Attribute(labels, concept);
			dqm.jku.trustkg.dsd.records.Record rec = new dqm.jku.trustkg.dsd.records.Record(concept);
			rec.addValueNeo4J(att, properties);
			
			list.addRecord(rec);		
		}
		
		return list;
	}
	
	private StatementResult getAllLabels() {
		StatementResult result = execute(cypher_get_all_labels);
		
		return result;
	}
	
	private StatementResult getAllPropertyKeys() {
		StatementResult result = execute(cypher_get_all_property_keys);
		
		return result;
	}
	
	private StatementResult getAllRelationshipTypes() {
		StatementResult result = execute(cypher_get_all_relationship_types);
		
		return result;
	}
	
	private int getNrRelationships() {
		StatementResult resultRelationships = execute(cypher_get_nr_edges);
		List<Record> records = resultRelationships.list();
		String s = records.get(0).get(0).toString();
		
		return Integer.parseInt(s);
	}
	
	private int getNrNodes() {
		StatementResult resultNodes = execute(cypher_get_nr_nodes);
		List<Record> records = resultNodes.list();
		String s =  records.get(0).get(0).toString();
		
		return Integer.parseInt(s);	
	}
	
	
	private boolean isFullyConnected() {
		StatementResult resultNodes = execute(cypher_get_unconnected_nodes);
		List<Record> records = resultNodes.list();
		
		if(records.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	private RecordList getNodeDegree(Concept c) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		StatementResult result = execute(cypher_get_degree_distribution);
		List<Record> records = result.list();		
		RecordList list = new RecordList();

		for(Record r : records) {
			Map<String, Object> map = r.asMap();		
			Collection<Object> coll = map.values();
			Object[] o = coll.toArray();
				
			String labels = getLabelString(o[0]);
			HashMap<String, String> properties = getPropertiesMap(o[0]);
			properties.put("Label:", labels);
			
			long counter = (Long) o[1];
			
			Attribute att = new Attribute(properties.toString(), c);
			dqm.jku.trustkg.dsd.records.Record rec = new dqm.jku.trustkg.dsd.records.Record(c);
			rec.addValueNeo4J(att, counter);
			
			list.addRecord(rec);
		}	
		
		return list;
	}
	
	private StatementResult execute(String statement) {
		StatementResult result = session.run(statement);
		
		return result;
	}
	
	@SuppressWarnings("unused")
	private int getNodesAndRelationships() {
		StatementResult result = execute(cypher_get_all_nodes_and_relationships);
		List<Record> records = result.list();
		
		//NOT IMPLEMENTED
		
		return 0;
	}
	
	private String getLabelString(Object o) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		ArrayList<?> labelsGeneric = new ArrayList<>();
		ArrayList<String> labels = new ArrayList<>();;
		
		Field field = o.getClass().getDeclaredField("labels");
		field.setAccessible(true);				
		labelsGeneric = (ArrayList<?>) field.get(o);
		
		for(int i = 0; i < labelsGeneric.size(); i++) {
			labels.add(labelsGeneric.get(i).toString());
		}
		
		return labels.toString();	
	}
	
	private HashMap<String, String> getPropertiesMap(Object o) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HashMap<?, ?> propertiesGeneric = new HashMap<>();
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
