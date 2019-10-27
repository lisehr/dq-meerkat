package dqm.jku.trustkg.demos.lisa;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;

public class TributechDataProfile {
	private static final int RDP_SIZE = 5000;
	private static final String[] FILENAMES = {
			"Acceleration breaking or forward", 
			"Acceleration side to side", 
			"Acceleration up or down",
			"Device Voltage",
			"Engine RPM",
			"Engine Speed",
			"Engine Temperature"};
	
	private static HashMap<Datasource, ConnectorCSV> dss = new HashMap<Datasource, ConnectorCSV>();

	public static void main(String[] args) throws IOException, NoSuchMethodException {
	    int[] indices = {0,2,3,4,5,6};
	    loadDataSets(indices);
		
		for(Datasource ds : dss.keySet()) {
	    	for (Concept c : ds.getConceptsAndAssociations()) {
	    		ConnectorCSV conn = dss.get(ds);
	    		RecordList rs = conn.getPartialRecordSet(c, 0, RDP_SIZE);
	    		Attribute val = c.getAttribute("value");
	    		val.annotateProfile(rs);
	    		System.out.println("=== " + ds.getLabel() + "===");
	    		val.printAnnotatedProfile();
	  	    }
	    }
	}
	
	private static void loadDataSets(int[] ind) throws IOException {
		for(int i : ind) {		
			String fname = FILENAMES[i];
			String fpath = "src/main/java/dqm/jku/trustkg/resources/Telematic Device Report - " + fname + ".csv";
			ConnectorCSV conn = new ConnectorCSV(fpath, ",", "\n", fname, true);
			Datasource ds = conn.loadSchema();
			dss.put(ds, conn);
		}
	}
}
