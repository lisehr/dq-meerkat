package dqm.jku.dqmeerkat.demos.evalsAndPubs;

import java.io.IOException;

import dqm.jku.dqmeerkat.connectors.ConnectorMySQL;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;

public class MySQLDBDemo {
	private static final int AMOUNT = 5000;
	
	public static void main(String[] args) throws IOException, NoSuchMethodException {
//		InfluxDBConnection influx = new InfluxDBConnection();
		int noRecs = 0;
//	    int offset = 0;
		ConnectorMySQL conn = ConnectorMySQL.getInstance("jdbc:mysql://localhost:3366/", "Northwind", "dquser", "dataQ4T!_pw");	
		Concept testCon = null;
		
		Datasource ds = conn.loadSchema();
	    for (Concept c : ds.getConceptsAndAssociations()) {
	      testCon = c;
	      RecordList rs = conn.getPartialRecordList(c, 0, AMOUNT);
	      for (Attribute a : c.getSortedAttributes()) {
	        a.annotateProfile(rs);
	        a.printAnnotatedProfile();
	      }
	    }
	    noRecs = conn.getNrRecords(testCon);
	    System.out.println(noRecs);
//	    ds.addProfileToInflux(influx);

	}
}
