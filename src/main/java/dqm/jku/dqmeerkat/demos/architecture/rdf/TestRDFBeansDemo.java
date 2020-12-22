package dqm.jku.dqmeerkat.demos.architecture.rdf;

import java.io.File;
import java.io.IOException;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import dqm.jku.dqmeerkat.blockchain.standardchain.BlockChain;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

/**
 * Test class for RDFBeans functionality
 * 
 * @author optimusseptim
 *
 */
public class TestRDFBeansDemo {
//  private static final boolean DEBUG = false;

  @SuppressWarnings("deprecation")
  public static void main(String args[]) throws Exception {
    DSConnector conn = FileSelectionUtil.connectToCSV(1);

    File dataDir = new File("./testrepo");
    SailRepository repo = new SailRepository(new NativeStore(dataDir));
    repo.initialize();

    try (RepositoryConnection con = repo.getConnection()) {
      RDFBeanManager manager = new RDFBeanManager(con);

      Datasource ds;
      ds = conn.loadSchema();
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

      CloseableIteration<Datasource, Exception> iter = manager.getAll(Datasource.class);
      while (iter.hasNext()) {
        Datasource res = iter.next();
        System.out.println(res.getURI() + " is equal to original Datasource? " + res.equals(ds));
      }
      iter.close();

      // Testing blockchain aspect
      BlockChain bc = new BlockChain(5, "test");
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
