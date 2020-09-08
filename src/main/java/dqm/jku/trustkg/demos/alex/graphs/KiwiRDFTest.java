package dqm.jku.trustkg.demos.alex.graphs;

import java.io.IOException;

import org.apache.marmotta.kiwi.config.KiWiConfiguration;
import org.apache.marmotta.kiwi.persistence.KiWiDialect;
import org.apache.marmotta.kiwi.persistence.pgsql.PostgreSQLDialect;
import org.apache.marmotta.kiwi.sail.KiWiStore;
import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.SailException;

import dqm.jku.trustkg.blockchain.standardchain.BlockChain;
import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.util.FileSelectionUtil;

public class KiwiRDFTest {
	private static final int FILENO = 1;

	public static void main(String args[]) throws RepositoryException, IOException, NoSuchMethodException, RDFBeanException {
		KiWiDialect dialect = new PostgreSQLDialect();
		KiWiConfiguration config = new KiWiConfiguration("test", "jdbc:postgresql://localhost:5432/postgres", "postgres", "test", dialect);
		KiWiStore store = new KiWiStore(config);

		// Create Connection to CSV Connector
		DSConnector conn = FileSelectionUtil.connectToCSV(FILENO);

		// Create Schema from it
		Datasource ds;
		Repository bcRep = null;
		RepositoryConnection bcConn = null;
		RepositoryResult<Statement> result = null;

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

		// two separate repositories for the data and the blockchain
		bcRep = new SailRepository((Sail) store);
		bcConn = bcRep.getConnection();

		RDFBeanManager bcmanager = new RDFBeanManager( bcConn);

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

		result = ((org.eclipse.rdf4j.repository.RepositoryConnection) bcConn).getStatements(null, null, null);

		// proof that the data + blockchain have been stored in the database
		while (result.hasNext()) {
			Statement st = result.next();
			System.out.println("db contains data: " + st);
		}

	}
	
	protected Sail createSail(KiWiStore store) throws SailException, org.openrdf.sail.SailException {
	  store.setDropTablesOnShutdown(true);
	  store.initialize();
	  //return store;
	  return null;
	}

}
