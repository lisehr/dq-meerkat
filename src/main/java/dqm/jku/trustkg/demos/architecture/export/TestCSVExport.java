package dqm.jku.trustkg.demos.architecture.export;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.util.FileSelectionUtil;
import dqm.jku.trustkg.util.export.ExportUtil;

/**
 * Test class for export functionality for CSV files
 * 
 * @author optimusseptim
 *
 */
public class TestCSVExport {
  private static final boolean DEBUG = false;

  public static void main(String args[]) throws IOException, NoSuchMethodException {
    DSConnector conn = FileSelectionUtil.connectToCSV(3);

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

      System.out.println("Creating Data Profile...");

      for (Concept c : ds.getConcepts()) {
        RecordList rs = conn.getRecordList(c);
        for (Attribute a : c.getAttributes()) a.annotateProfile(rs);
        System.out.println("Done!");
      }
      System.out.println();
      System.out.println("Exporting Data Profile...");
      ExportUtil.exportToCSV(ds);
      System.out.println("Done!");

    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }
  }
}
