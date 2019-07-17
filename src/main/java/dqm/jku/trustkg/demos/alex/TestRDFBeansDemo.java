package dqm.jku.trustkg.demos.alex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.streampipes.empire.pinto.RDFMapper;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;

public class TestRDFBeansDemo {
  private static final String PREFIX = "src/main/java/dqm/jku/trustkg/resources/";
//  private static final boolean DEBUG = false;
  
  public static void main(String args[]) throws IOException {
    // walk resources package to make a selection on which csv file should be used for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();
    
    DSInstanceConnector conn = new ConnectorCSV(
        files.get(17).toString(), ",", "\n",
        "Test", true);

    RDFMapper mapper = RDFMapper.create();

    Datasource ds;
    try {
      ds = conn.loadSchema();
      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());

        for (Attribute a : c.getAttributes()) {
          System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
        }
        System.out.println();
        System.out.println(mapper.writeValue(c));

      }
      
    } catch (IOException e) {
      
    }
  }
}
