package dqm.jku.dqmeerkat.demos.graphmodels;

import java.io.IOException;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import dqm.jku.dqmeerkat.blockchain.standardchain.BlockChain;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.graphdb.EmbeddedGraphDB;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

public class StoreDataRDFSourceSelector {
	private static final int FILENO = 2;

	public static void main(String args[]) throws NoSuchMethodException, IOException {
		// Create Connection to CSV Connector
		DSConnector conn = FileSelectionUtil.getConnectorCSV(FILENO);

		// Create Schema from it
		Datasource ds;
		Repository bcRep = null;
		RepositoryConnection bcConn = null;
		RepositoryResult<Statement> result = null;
		
		try (EmbeddedGraphDB db = new dqm.jku.dqmeerkat.graphdb.EmbeddedGraphDB("kg-repo")) {

			ds = conn.loadSchema();

			ModelBuilder builder = new ModelBuilder();
			// builder.setNamespace("ex", "http://example.com/");

			for (Concept c : ds.getConcepts()) {
				for (Attribute a : c.getAttributes()) {
					builder.namedGraph(":testGraph").subject(c.toString()).add(RDF.TYPE, a.toString());
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

			// activate for first time creation
			db.createRepositoryIfNotExists("kg-repo");

			// two separate repositories for the data and the blockchain
			bcRep = db.getRepository("kg-repo");
			bcConn = bcRep.getConnection();

			RDFBeanManager bcmanager = new RDFBeanManager(bcConn);

			// annotating the data quality profile
			for (Concept c : ds.getConcepts()) {
				System.out.println(c.getURI());
				RecordList rs = conn.getPartialRecordList(c, 0, 5000);
				for (Attribute a : c.getAttributes()) {
					a.annotateProfile(rs);

					System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
					a.printAnnotatedProfile();
				}
				System.out.println();
			}

			BlockChain bc = new BlockChain(2, "http://example.com/blockchain/" + ds.getLabel().replaceAll("\\s+", "") + "BC");
			ds.fillBlockChain(bc);
			System.out.println(bc.isChainValid());

			bcmanager.add(bc);

			result = bcConn.getStatements(null, null, null);

			// proof that the data + blockchain have been stored in the database
			while (result.hasNext()) {
				Statement st = result.next();
				System.out.println("db contains data: " + st);
			}
		} catch (IOException e) {
			System.err.println("Could not load Schema!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		}
}
