package dqm.jku.dqmeerkat.demos.architecture.multicolumn;

import java.io.IOException;
import java.util.Iterator;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

/**
 * Test class for Multicolumn Metrics using the Popular Baby Names as data source file.
 * 
 * @author optimusseptim
 *
 */
public class TestCSVMultiColumnPopularBabyNames {
  private static final boolean DEBUG = false;

  public static void main(String args[]) throws IOException, NoSuchMethodException {
    DSConnector conn = FileSelectionUtil.getConnectorCSV(Constants.FileName.popularBabyNames.getPath());

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
        RecordList rs = conn.getPartialRecordList(c, 0, 10000);
        System.out.println(c.getURI());
        c.annotateProfile(rs);
        c.printAnnotatedProfile();
        System.out.println();
      }

    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }
  }
}
