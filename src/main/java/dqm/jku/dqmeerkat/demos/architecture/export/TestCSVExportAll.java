package dqm.jku.dqmeerkat.demos.architecture.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import dqm.jku.dqmeerkat.util.export.ExportUtil;

/**
 * Test class for export functionality for all CSV files in the folder
 * 
 * @author optimusseptim
 *
 */
public class TestCSVExportAll {
  private static final boolean DEBUG = false; // enables debug prints in this class
  private static final boolean EXTENSIVE_PRINT = false; // prints complete hierarchy of Sources
  private static final String CSV_PATH = "src/main/java/dqm/jku/dqmeerkat/resources/csv/";
  private static final int RDP_SIZE = 5000;

  public static void main(String args[]) throws IOException, NoSuchMethodException {
    File directory = new File(CSV_PATH);
    int fileCount = directory.list().length;
    DSConnector conn;

    List<Datasource> dss = new ArrayList<>();
    Datasource ds;

    for (int i = 1; i <= fileCount; i++) {
      try {
        conn = FileSelectionUtil.connectToCSV(i);
        ds = conn.loadSchema();
        if (EXTENSIVE_PRINT) {
          for (Concept c : ds.getConcepts()) {
            System.out.println(c.getURI());

            for (Attribute a : c.getAttributes()) {
              System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
            }
            System.out.println();
          }
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

        System.out.println("Creating Data Profile for " + ds.getLabel() + "...");

        for (Concept c : ds.getConcepts()) {
          RecordList rs = conn.getPartialRecordList(c, 0, RDP_SIZE);
          for (Attribute a : c.getAttributes()) a.annotateProfile(rs);
          System.out.println("Done!");
        }
        System.out.println();
        dss.add(ds);

      } catch (IOException e) {
        System.err.println("Could not load Schema!");
      }
    }

    System.out.println("Exporting Data Profile...");
    ExportUtil.exportToCSV(dss);
    System.out.println("Done!");

  }

}
