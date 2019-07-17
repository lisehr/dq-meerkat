package dqm.jku.trustkg.demos.alex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;

public class TestCSVDataConverterSourceSelector {
  private static final String PREFIX = "src/main/java/dqm/jku/trustkg/resources/";
  private static final boolean DEBUG = false;
  
  public static void main(String args[]) throws IOException {
    // walk resources package to make a selection on which csv file should be used for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();
    
    DSInstanceConnector conn = new ConnectorCSV(
        files.get(1).toString(), ",", "\n",
        "Test", true);
    
    Datasource ds;
    try {
      ds = conn.loadSchema();
      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());

        for (Attribute a : c.getAttributes()) {
          System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
        }
        System.out.println();
      }
      
      if (DEBUG) {
        for (Concept c : ds.getConcepts()) {
          Iterator<Record> rIt = conn.getRecords(c);
          while (rIt.hasNext()) {
            Record next = rIt.next();
            System.out.println(next.toString());
          }
          System.out.println();
        }
      }

      
      System.out.println("Changes on the Scheme:");

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



    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }
  }
}
