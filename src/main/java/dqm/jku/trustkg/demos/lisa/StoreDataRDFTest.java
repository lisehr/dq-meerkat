package dqm.jku.trustkg.demos.lisa;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import dqm.jku.trustkg.blockchain.BlockChain;
import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordSet;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;

import dqm.jku.trustkg.graphdb.*;

@SuppressWarnings("unused")
public class StoreDataRDFTest {
	public static void main(String args[]) {
		// Create Connection to CSV Connector
		DSInstanceConnector conn = new ConnectorCSV(
				"src/main/java/dqm/jku/trustkg/resources/Telematic Device Report - Device Voltage.csv", ",", "\n",
				"Device Voltage", true);

		// Create Schema from it
		Datasource ds;
		try {
			ds = conn.loadSchema();

			ModelBuilder builder = new ModelBuilder();
			builder.setNamespace("ex", "http://example.com/");

			for (Concept c : ds.getConcepts()) {
				for (Attribute a : c.getAttributes()) {
					builder.namedGraph("ex:testGraph").subject("ex:" + c.toString()).add(RDF.TYPE,
							"ex:" + a.toString());
				}

			}

			System.out.println();

			Model m = builder.build();
			// the size of contexts indicates how many graphs are stored in this model
			// System.out.println(m.contexts().size());

			for (Resource context : m.contexts()) {
				System.out.println("Graph " + context + " contains: ");
				Rio.write(m.filter(null, null, null, context), System.out, RDFFormat.TURTLE);
			}

			EmbeddedGraphDB db = new dqm.jku.trustkg.graphdb.EmbeddedGraphDB("test2");

			// activate for first time creation
			db.createRepository("test2");
			db.createRepository("test3");
			
			// two separate repositories for the data and the blockchain
			Repository testRep = db.getRepository("test2");
			Repository bcRep = db.getRepository("test3");
			RepositoryConnection repConn = testRep.getConnection();
			RepositoryConnection bcConn = bcRep.getConnection();

			RDFBeanManager manager = new RDFBeanManager(repConn);
			RDFBeanManager bcmanager = new RDFBeanManager(bcConn);

			// annotating the data quality profile
			for (Concept c : ds.getConcepts()) {
				System.out.println(c.getURI());
				RecordSet rs = conn.getRecordSet(c);
				for (Attribute a : c.getAttributes()) {
					a.annotateProfile(rs);

					System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
					a.printAnnotatedProfile();
				}
				System.out.println();
			}

			manager.add(ds);

			BlockChain bc = new BlockChain(5, "test");
			ds.fillBlockChain(bc);
			System.out.println(bc.isChainValid());

			bcmanager.add(bc);
			
			// proof that the data + blockchain have been stored in the database
			try (RepositoryResult<Statement> result = repConn.getStatements(null, null, null);
					RepositoryResult<Statement> bcResult = bcConn.getStatements(null, null, null);) {
				while (result.hasNext()) {
					Statement st = result.next();
					System.out.println("db contains data: " + st);
				}
				while (bcResult.hasNext()) {
					Statement st = bcResult.next();
					System.out.println("db contains blockchain data: " + st);
				}
			}

			finally {
				db.close();
			}

		} catch (IOException e) {
			System.err.println("Could not load Schema!");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}