package dqm.jku.trustkg.demos.kathi;

import dqm.jku.trustkg.connectors.ConnectorNeo4J;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;

import java.io.IOException;
import java.util.Iterator;

public class TestNeo4j {

	public static void main(String[] args) throws ClassNotFoundException {
		
		String label = "neo4j";
		String uri = "bolt://localhost:7687";
		
		ConnectorNeo4J conn = ConnectorNeo4J.getInstance("bolt://localhost:7687", "neo4j", "password", label);
		//ConnectorNeo4J conn = ConnectorNeo4J.getInstance("bolt+routing://f6087a21.databases.neo4j.io:7687", "stackoverflow", "stackoverflow", label);
		

	    Datasource ds;
		
	    try {
	    	ds = conn.loadSchema(uri, label);
	    	System.out.println("Schema: ");
	    	ds.printStructure();
	    	
	    	System.out.println("All Nodes: ");
	    	Iterator<Record> recList = conn.getRecords(ds.getConcept(label));
	    	while (recList.hasNext()) {
	    		Record next = recList.next();
	    		System.out.println(next.toStringNeo4J());
	    	}
	    	
	    	System.out.println();
	    	System.out.println("All Nodes and their number of Relationships");
	    	recList = conn.getDegreeDistribution(ds.getConcept(label));
	    	while (recList.hasNext()) {
	    		Record next = recList.next();
	    		System.out.println(next.toStringNeo4J());
	    	} 
	    	
	    	System.out.println();
	    	System.out.println("Number of Nodes: " + conn.getNrRecords(ds.getConcept(label)));
	    	System.out.println();
	    	System.out.println("Number of Relationships: " + conn.getNrRelationships(ds.getConcept(label)));
	    	System.out.println();
	    	System.out.println("Graph is fully connected: " + conn.isGraphFullyConnected(ds.getConcept(label)));
	    			    		 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.close(); 
		}
		
		
		
		
	}

}
