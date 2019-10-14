package dqm.jku.trustkg.demos.lisa;

import java.io.IOException;

import dqm.jku.trustkg.connectors.ConnectorMySQL;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;

public class MySQLDBDemo {
	private static final int AMOUNT = 10;
	private static final int FILEINDEX = 21;
	private static final String NAME = "northwind";

	public static void main(String[] args) throws IOException, NoSuchMethodException {
		InfluxDBConnection influx = new InfluxDBConnection();
		int noRecs = 0;
	    int offset = 0;
		ConnectorMySQL conn = ConnectorMySQL.getInstance("jdbc:mysql://localhost:3366/", "Northwind", "dquser", "dataQ4T!_pw");
		
		Concept testCon = null;
		
		Datasource ds = conn.loadSchema();
	    for (Concept c : ds.getConceptsAndAssociations()) {
	      testCon = c;
	      RecordSet rs = conn.getPartialRecordSet(c, 0, AMOUNT);
	      for (Attribute a : c.getSortedAttributes()) {
	        a.annotateProfile(rs);
	        a.printAnnotatedProfile();
	      }
	    }
	    noRecs = conn.getNrRecords(testCon);
	    ds.addProfileToInflux(influx);

	}
}
