package dqm.jku.trustkg.demos.alex.qualityfeatures;

import java.io.IOException;

import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.NullValuesPercentage;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo.Digits;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.dependency.KeyCandidate;
import dqm.jku.trustkg.util.FileSelectionUtil;

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
