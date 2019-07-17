package dqm.jku.trustkg.demos.alex;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
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

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;

public class TestRDFBeansDemo {
  private static final String PREFIX = "src/main/java/dqm/jku/trustkg/resources/";
//  private static final boolean DEBUG = false;
  
  @SuppressWarnings("deprecation")
  public static void main(String args[]) throws Exception {
    // walk resources package to make a selection on which csv file should be used for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();
    
    DSInstanceConnector conn = new ConnectorCSV(
        files.get(17).toString(), ",", "\n",
        "Test", true);

    
    File dataDir = new File("./testrepo");      
    SailRepository repo = new SailRepository(new NativeStore(dataDir));
    repo.initialize();
    try (RepositoryConnection con = repo.getConnection()) {
      RDFBeanManager manager = new RDFBeanManager(con);         

      Datasource ds;
      ds = conn.loadSchema();
      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());

        for (Attribute a : c.getAttributes()) {
          System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
          manager.add(a);
        }
        System.out.println();
      }
      
      Person p = new Person();
      p.setBirthday(new Date(1,2,3));
      p.setEmail("test");
      p.setHomepage(new URI("http://example.com"));
      p.setId("hello");
      p.setName("tom");
      manager.add(p);
      
      CloseableIteration<Attribute, Exception> iter = manager.getAll(Attribute.class);
      while (iter.hasNext()) {
        Attribute p2 = iter.next();
         System.out.println(p2.getURI());
      }
      iter.close();
    } catch (IOException e) {
      
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (RDFBeanException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
