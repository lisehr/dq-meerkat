package dqm.jku.dqmeerkat.demos.architecture.qualityfeatures;

import java.io.IOException;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NullValuesPercentage;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo.Digits;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.dependency.KeyCandidate;
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
          dp.addStatistic(new NullValuesPercentage(dp));
          dp.addStatistic(new Digits(dp));
          dp.addStatistic(new KeyCandidate(dp));
          for (ProfileStatistic m : dp.getStatistics()) m.calculation(rs, null);
          dp.printProfile();
        }
        System.out.println();
      }

    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }
  }
}
