package dqm.jku.dqmeerkat.demos.architecture.qualityfeatures;

import java.io.IOException;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingmetrics.ProfileMetric;
import dqm.jku.dqmeerkat.quality.profilingmetrics.singlecolumn.cardinality.NullValuesPercentage;
import dqm.jku.dqmeerkat.quality.profilingmetrics.singlecolumn.datatypeinfo.Digits;
import dqm.jku.dqmeerkat.quality.profilingmetrics.singlecolumn.dependency.KeyCandidate;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

/**
 * Demo class for testing the dependency checks for DP metrics
 * 
 * @author optimusseptim
 *
 */
public class DependencyCheckDemo {
  public static void main(String args[]) throws IOException, NoSuchMethodException {
    DSConnector conn = FileSelectionUtil.connectToCSV(7);

    Datasource ds;
    try {
      ds = conn.loadSchema();

      System.out.println("Changes on the Scheme:");

      for (Concept c : ds.getConcepts()) {
        System.out.println(c.getURI());
        RecordList rs = conn.getRecordList(c);
        for (Attribute a : c.getAttributes()) {
          DataProfile dp = new DataProfile();
          dp.setElem(a);
          dp.setURI(a.getURI() + "/profile");
          dp.addMetric(new NullValuesPercentage(dp));
          dp.addMetric(new Digits(dp));
          dp.addMetric(new KeyCandidate(dp));
          for (ProfileMetric m : dp.getMetrics()) m.calculation(rs, null);
          dp.printProfile();
        }
        System.out.println();
      }

    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }
  }
}
