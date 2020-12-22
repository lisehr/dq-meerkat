package dqm.jku.dqmeerkat.demos.graphmodels;

import java.io.IOException;

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
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import dqm.jku.dqmeerkat.blockchain.standardchain.BlockChain;
import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.graphdb.EmbeddedGraphDB;

public class StoreDataRDFTest {
  public static void main(String args[]) throws NoSuchMethodException {
    // Create Connection to CSV Connector
    DSConnector conn = new ConnectorCSV("src/main/java/dqm/jku/dqmeerkat/resources/csv/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);

    // Create Schema from it
    Datasource ds;
    try {
      ds = conn.loadSchema();

      ModelBuilder builder = new ModelBuilder();
     // builder.setNamespace("ex", "http://example.com/");

      for (Concept c : ds.getConcepts()) {
        for (Attribute a : c.getAttributes()) {
          builder.namedGraph(":testGraph").subject(c.toString()).add(RDF.TYPE,a.toString());
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

      EmbeddedGraphDB db = new dqm.jku.dqmeerkat.graphdb.EmbeddedGraphDB("test2");
      

      // activate for first time creation
      db.createRepositoryIfNotExists("test2");
      db.createRepositoryIfNotExists("test3");

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
        RecordList rs = conn.getRecordList(c);
        for (Attribute a : c.getAttributes()) {
          a.annotateProfile(rs);

          System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
          a.printAnnotatedProfile();
        }
        System.out.println();
      }

      manager.add(ds);

      BlockChain bc = new BlockChain(2, "http://example.com/blockchain/DeviceVoltageBC");
      ds.fillBlockChain(bc);
      System.out.println(bc.isChainValid());

      bcmanager.add(bc);

      // proof that the data + blockchain have been stored in the database
      try (RepositoryResult<Statement> result = repConn.getStatements(null, null, null); RepositoryResult<Statement> bcResult = bcConn.getStatements(null, null, null);) {
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
    	testRep.shutDown();
    	bcRep.shutDown(); 
    	repConn.close();
    	bcConn.close();
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