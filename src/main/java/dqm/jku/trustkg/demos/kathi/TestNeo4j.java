package dqm.jku.trustkg.demos.kathi;

import dqm.jku.trustkg.connectors.ConnectorNeo4J;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;

public class TestNeo4j {

	public static void main(String[] args)  {
		
		String label = "neo4j";
		String uri = "bolt://localhost:7687";
		
		ConnectorNeo4J conn = ConnectorNeo4J.getInstance(uri, "neo4j", "password", label);
	    Datasource ds;
		
	    try {
	    	ds = conn.loadSchema(uri, label);
	    	System.out.println("Schema: ");
	    	ds.printStructure();

			for (Concept c : ds.getConceptsAndAssociations()) {
				RecordList rs = conn.getRecordList(c);
				for (Attribute a : c.getSortedAttributes()) {
					a.annotateProfile(rs);
					System.out.println("Attribute a: " + a.toString());
					a.printAnnotatedProfile();
				}
			}

			//Iterator<Record> record = conn.getDegreeDistribution(ds.getConcept(label));

	    	System.out.println("Number of Nodes: " + conn.getNrRecords(ds.getConcept(label)));
	    	System.out.println("Number of Relationships: " + conn.getNrRelationships(ds.getConcept(label)));
	    	System.out.println("Graph has unconnected nodes: " + conn.isGraphConnected(ds.getConcept(label)));

	    	
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} finally {
			conn.close(); 
		}
		
	}

}
