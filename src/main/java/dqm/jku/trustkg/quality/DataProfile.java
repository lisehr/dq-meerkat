package dqm.jku.trustkg.quality;

import java.util.HashMap;
import java.util.Map;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength.*;

public class DataProfile {
  private Map<String, ProfileMetric> metrics = new HashMap<>();

  public DataProfile(RecordSet rs, DSDElement d) {
    createStandardProfile();
    calculateInitialProfile(rs, d);
  }

  /**
   * calculates an initial data profile based on the values inserted in the
   * standard profile
   * 
   * @param rs the set of records used for calculation
   * @param d  the dsd element to be annotated (currently only Attribute for
   *           single column metrics)
   */
  private void calculateInitialProfile(RecordSet rs, DSDElement d) {
    for (ProfileMetric p : metrics.values()) {
      p.calculation(rs, (Attribute) d);
    }
  }

  /**
   * creates a standard data profile on which calculations can be made
   */
  private void createStandardProfile() {
    ProfileMetric min = new Minimum();
    metrics.put(min.getLabel(), min);
    ProfileMetric max = new Maximum();
    metrics.put(max.getLabel(), max);
    ProfileMetric avg = new Average();
    metrics.put(avg.getLabel(), avg);
    ProfileMetric med = new Median();
    metrics.put(med.getLabel(), med);
  }

  /**
   * Method for printing out the data profile
   */
  public void printProfile() {
    System.out.println("Data Profile:");
    if (metrics.values().stream().anyMatch(p -> p.getValueClass().equals(String.class))) System.out.println("Strings use String length for value length metrics!");
    for (ProfileMetric p : metrics.values()) {
      System.out.println(p.toString());
    }
    System.out.println();
  }

}
