package dqm.jku.trustkg.demos.alex.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.util.FileSelectionUtil;
import dqm.jku.trustkg.util.export.ExportUtil;

public class TestCSVExportAll {
  private static final boolean DEBUG = false;
  private static final boolean EXTENSIVE_PRINT = true;
  private static final String CSV_PATH = "src/main/java/dqm/jku/trustkg/resources/csv/";

  public static void main(String args[]) throws IOException, NoSuchMethodException {
    File directory = new File(CSV_PATH);
    int fileCount = directory.list().length;
    DSInstanceConnector conn;

    List<Datasource> dss = new ArrayList<>();
    Datasource ds;

    for (int i = 3; i <= fileCount; i++) {
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
            RecordList rs = conn.getRecordSet(c);
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
