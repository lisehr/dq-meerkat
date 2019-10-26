package dqm.jku.trustkg.demos.lisa;

import java.io.IOException;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordSet;

public class MySQLDBDemo {
	private static final int AMOUNT = 5000;
	private static final int FILEINDEX = 21;

	public static void main(String[] args) throws IOException, NoSuchMethodException {
//		InfluxDBConnection influx = new InfluxDBConnection();
		int noRecs = 0;
	    int offset = 0;
//		ConnectorMySQL conn = ConnectorMySQL.getInstance("jdbc:mysql://localhost:3366/", "Northwind", "dquser", "dataQ4T!_pw");
	    ConnectorCSV conn = new ConnectorCSV("src/main/java/dqm/jku/trustkg/resources/Telematic Device Report - Acceleration side to side.csv", ",", "\n", "Device Voltage", true);

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
//	    ds.addProfileToInflux(influx);

	}
}
