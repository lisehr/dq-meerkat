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
  private boolean isNumeric;

  public DataProfile(RecordSet rs, DSDElement d, boolean isNumeric) {
    this.isNumeric = isNumeric;
    createStandardProfile();
    calculateInitialProfile(rs, d);
  }

  private void calculateInitialProfile(RecordSet rs, DSDElement d) {
    for (ProfileMetric p : metrics.values()) {
      p.calculation(rs, (Attribute) d);
    }
  }

  private void createStandardProfile() {
    if (isNumeric) {
      ProfileMetric min = new Minimum();
      metrics.put(min.getLabel(), min);
      ProfileMetric max = new Maximum();
      metrics.put(max.getLabel(), max);
      ProfileMetric avg = new Average();
      metrics.put(avg.getLabel(), avg);
      ProfileMetric med = new Median();
      metrics.put(med.getLabel(), med);
    }
    
  }

  public void printProfile() {
    System.out.println("Data Profile:");
    for (ProfileMetric p : metrics.values()) {
      System.out.println(p.toString());
    }
    System.out.println();
  }

  public void updateNumeric(boolean isNumeric) {
    this.isNumeric = isNumeric;
  }

}
