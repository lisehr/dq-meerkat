package dqm.jku.dqmeerkat.demos.evalsAndPubs;

import java.io.IOException;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.graphdb.EmbeddedGraphDB;
import dqm.jku.dqmeerkat.util.Constants;

public class GettingStartedVerbose {

	private static final int RDP_SIZE = 5000;

	public static void main(String[] args) throws IOException, NoSuchMethodException {
		// Create Connection to CSV Connector
		ConnectorCSV conn = new ConnectorCSV(Constants.RESOURCES_FOLDER + "csv/DataCoSupplyChainDataset.csv", ",", "\n", "SupplyChain");
	    
	    // Init KG by loading DSD elements
	    Datasource ds = conn.loadSchema();
	    
	    // Annotate reference data profile to KG
	    for (Concept c : ds.getConceptsAndAssociations()) {
			RecordList rs = conn.getPartialRecordList(c, 0, RDP_SIZE);
			for(Attribute a : c.getAttributes()) {
				a.annotateProfile(rs);
			}
		}
	    
	    // Persist KG to Embedded GraphDB
	    EmbeddedGraphDB db = new EmbeddedGraphDB(ds.getLabel());
	    db.createRepositoryIfNotExists(ds.getLabel());
	    RepositoryConnection repConn = db.getRepository(ds.getLabel()).getConnection();
	    repConn.add(ds.getGraphModel(new ModelBuilder()));
	    db.close();
	    
	    // Export KG to .ttl file
//	    FileOutputStream out = new FileOutputStream(Constants.RESSOURCES + "export/supplychain.ttl");
//	    Rio.write(ds.getGraphModel(), out, RDFFormat.TURTLE);
//	    out.close();
	}

}
