package dqm.jku.dqmeerkat.demos.graphmodels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.graphdb.EmbeddedGraphDB;

@SuppressWarnings("unused")
public class TestRDF4JModel {
  public static void main(String args[]) {
    // Create Connection to CSV Connector
    DSConnector conn = new ConnectorCSV("src/main/java/dqm/jku/dqmeerkat/resources/csv/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);

    // Create Schema from it
    Datasource ds;
    try {
      ds = conn.loadSchema();

      ModelBuilder builder = new ModelBuilder();
      builder.setNamespace("ex", "http://example.com/");

      for (Concept c : ds.getConcepts()) {
        for (Attribute a : c.getAttributes()) {
          builder.namedGraph("ex:testGraph").subject("ex:" + c.toString()).add(RDF.TYPE, "ex:" + a.toString());
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

      EmbeddedGraphDB db = new dqm.jku.dqmeerkat.graphdb.EmbeddedGraphDB("test");

      // activate for first time creation
      //db.createRepository("test");
      Repository testRep = db.getRepository("test");
      RepositoryConnection repConn = testRep.getConnection();

      // transforming a Java iterable collection to RDF
      ArrayList<Integer> a = new ArrayList<Integer>();
      a.add(1);
      a.add(2);
      a.add(3);
      repConn.add(m);
      Model m2 = RDFCollections.asRDF(a, null, m);

      // testing the Rio writing function
      FileOutputStream out = new FileOutputStream("//home//optimusseptim//graphdb_test//file.ttl");
      Rio.write(m, out, RDFFormat.TURTLE);
      out.close();

      try (RepositoryResult<Statement> result = repConn.getStatements(null, null, null);) {
        while (result.hasNext()) {
          Statement st = result.next();
          System.out.println("db contains: " + st);
        }
      } finally {
        db.close();
      }

    } catch (IOException e) {
      System.err.println("Could not load Schema!");
      e.printStackTrace();
    }

  }
}
