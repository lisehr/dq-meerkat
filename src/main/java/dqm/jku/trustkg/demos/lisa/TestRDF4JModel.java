package dqm.jku.trustkg.demos.lisa;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;

import dqm.jku.trustkg.graphdb.*;

public class TestRDF4JModel {
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

			EmbeddedGraphDB db = new dqm.jku.trustkg.graphdb.EmbeddedGraphDB("//home//lisa//graphdb_test");

			// activate for first time creation
			//db.createRepository("test");
			Repository testRep = db.getRepository("test");
			RepositoryConnection repConn = testRep.getConnection();
			
			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add(1);
			a.add(2);
			a.add(3);
			repConn.add(m);
			//sRDFCollections.asRDF(a, null, m, );
			
			// testing the Rio writing function
			//FileOutputStream out = new FileOutputStream("//home//lisa//graphdb_test//file.ttl");
			//Rio.write(m, out, RDFFormat.TURTLE);
			// out.close();

			try (RepositoryResult<Statement> result = repConn.getStatements(null, null, null);) {
				while (result.hasNext()) {
					Statement st = result.next();
					System.out.println("db contains: " + st);
				}
			}
			finally {
				db.close();
			}

		} catch (IOException e) {
			System.err.println("Could not load Schema!");
		}

	}
}
