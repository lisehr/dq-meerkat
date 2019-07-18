package dqm.jku.trustkg.demos.alex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import dqm.jku.trustkg.blockchain.BlockChain;
import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordSet;

public class TestRDFBeansDemo {
  private static final String PREFIX = "src/main/java/dqm/jku/trustkg/resources/";
//  private static final boolean DEBUG = false;

  @SuppressWarnings("deprecation")
  public static void main(String args[]) throws Exception {
    // walk resources package to make a selection on which csv file should be used
    // for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();

    DSInstanceConnector conn = new ConnectorCSV(files.get(17).toString(), ",", "\n", "Test", true);

    File dataDir = new File("./testrepo");
    SailRepository repo = new SailRepository(new NativeStore(dataDir));
    repo.initialize();

    try (RepositoryConnection con = repo.getConnection()) {
      RDFBeanManager manager = new RDFBeanManager(con);

      Datasource ds;
      ds = conn.loadSchema();
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

      CloseableIteration<Datasource, Exception> iter = manager.getAll(Datasource.class);
      while (iter.hasNext()) {
        Datasource res = iter.next();
        System.out.println(res.getURI() + " is equal to original Datasource? " + res.equals(ds));
      }
      iter.close();

      // Testing blockchain aspect
      BlockChain bc = new BlockChain();
      ds.fillBlockChain(bc);
      System.out.println(bc.isChainValid());

      manager.add(bc);

      CloseableIteration<BlockChain, Exception> iterBC = manager.getAll(BlockChain.class);
      while (iterBC.hasNext()) {
        BlockChain res = iterBC.next();
        System.out.println(res.isChainValid());
        System.out.println("Restored BlockChain is equal to original Datasource? " + res.equals(bc));
      }
      iterBC.close();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (RepositoryException e) {
      e.printStackTrace();
    } catch (RDFBeanException e) {
      e.printStackTrace();
    }
  }
}
