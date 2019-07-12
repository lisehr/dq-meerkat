package dqm.jku.trustkg.demos.alex;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;

public class TestCSVDataConverterSimpleSource {
  public static void main(String args[]) {
    DSInstanceConnector conn = new ConnectorCSV(
        "src/main/java/dqm/jku/trustkg/resources/test.csv", ",", "\n",
        "Test", true);
    
    Datasource ds;
    try {
      ds = conn.loadSchema();
      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());

        for (Attribute a : c.getAttributes()) {
          System.out.println(a.getDataType().toString() + "\t" + a.getURI());
        }
        System.out.println();
      }
      
      for (Concept c : ds.getConcepts()) {
        Iterator<Record> rIt = conn.getRecords(c);
        while (rIt.hasNext()) {
          Record next = rIt.next();
          System.out.println(next.toString());
        }        
        System.out.println();
      }
      
      System.out.println("Changes on the Scheme:");

      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());
        RecordSet rs = conn.getRecordSet(c);
        for (Attribute a : c.getAttributes()) {
          a.annotateProfile(rs);        
          
          System.out.println(a.getDataType().toString() + "\t" + a.getURI());
        }
        System.out.println();
      }



    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }
  }
}
