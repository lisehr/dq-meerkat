package dqm.jku.trustkg.demos.alex.connector;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.util.FileSelectionUtil;

public class TestCSVDataConverterSourceSelector {
  private static final boolean DEBUG = false;
  
  public static void main(String args[]) throws IOException, NoSuchMethodException {
    DSInstanceConnector conn = FileSelectionUtil.connectToCSV(7);
    
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
        RecordList rs = conn.getRecordSet(c);
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
