package dqm.jku.dqmeerkat.demos.kathi;

import dqm.jku.dqmeerkat.connectors.ConnectorNeo4J;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;

public class TestNeo4j {

	public static void main(String[] args)  {

		// TODO Merge master

		String label = "neo4j";
		String uri = "bolt://localhost:7687";
		
		ConnectorNeo4J conn = ConnectorNeo4J.getInstance(uri, "neo4j", "password", label);
	    Datasource ds;
		
	    try {
	    	ds = conn.loadSchema(uri, label);
	    	System.out.println("Schema: ");
	    	ds.printStructure();

//			for (Concept c : ds.getConcepts()) {
//				RecordList rs = conn.getRecordList(c);
//
//				for(Attribute a : c.getSortedAttributes()) {
//					RecordList newList = rs.getValues(c, a.getLabel());
//
//					if(newList.size() > 0) {
//						c.annotateProfile(newList);
//					} else {
//						c.emptyDataProfile();
//					}
//					c.printAnnotatedProfileNeo4J(a);
//
//				}
//			}

			for (Concept c : ds.getConcepts()) {
				RecordList rs = conn.getDegreeDistribution(c);
				if(rs != null) {
					for (Attribute a : c.getSortedAttributes()) {
						RecordList newList = rs.getValues(c, a.getLabel());

						if(newList.size() > 0) {
							c.annotateProfile(newList);
						} else {
							// TODO: emptyDataProfile is not existing in DSD concept...
							//c.emptyDataProfile();
						}
						// TODO: printAnnotatedProfileNeo4J is not existing in DSD concept...
						//c.printAnnotatedProfileNeo4J(a);
					}
				}
			}

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} finally {
			conn.close(); 
		}
		
	}

}
