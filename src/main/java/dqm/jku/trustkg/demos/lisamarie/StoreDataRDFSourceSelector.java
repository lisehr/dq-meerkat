package dqm.jku.trustkg.demos.lisamarie;

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

import dqm.jku.trustkg.blockchain.standardchain.BlockChain;
import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.graphdb.EmbeddedGraphDB;
import dqm.jku.trustkg.util.FileSelectionUtil;

public class StoreDataRDFSourceSelector {
	private static final int FILENO = 13;

	  public static void main(String args[]) throws NoSuchMethodException, IOException {
		    // Create Connection to CSV Connector
		    DSConnector conn = FileSelectionUtil.connectToCSV(FILENO);

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

		      EmbeddedGraphDB db = new dqm.jku.trustkg.graphdb.EmbeddedGraphDB("test2");
		      

		      // activate for first time creation
		      db.createRepositoryIfNotExists("test" + FILENO);

		      // two separate repositories for the data and the blockchain
		      Repository bcRep = db.getRepository("test" + FILENO);
		      RepositoryConnection bcConn = bcRep.getConnection();

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

		      BlockChain bc = new BlockChain(2, "http://example.com/blockchain/" + ds.getLabel().replaceAll("\\s+", "") + "BC");
		      ds.fillBlockChain(bc);
		      System.out.println(bc.isChainValid());

		      bcmanager.add(bc);

		      // proof that the data + blockchain have been stored in the database
		      try (RepositoryResult<Statement> result = bcConn.getStatements(null, null, null)) {
		        while (result.hasNext()) {
		          Statement st = result.next();
		          System.out.println("db contains data: " + st);
		        }
		      }

		      finally {
		    	bcRep.shutDown(); 
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
