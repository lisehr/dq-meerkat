package dqm.jku.trustkg.quality;

import java.util.HashMap;
import java.util.Map;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlevalue.*;


public class DataProfile {
  private Map<String, ProfileMetric> metrics = new HashMap<>();
  
  public DataProfile(RecordSet rs, DSDElement d) {
    createStandardProfile();
    calculateInitialProfile(rs, d);
  }

  private void calculateInitialProfile(RecordSet rs, DSDElement d) {
    for (ProfileMetric p : metrics.values()) {
      p.calculation(rs, (Attribute)d);
    }
  }

  private void createStandardProfile() {
    ProfileMetric minimum = new Minimum();
    metrics.put(minimum.getLabel(), minimum);
  }
  
  public void printProfile() {
    System.out.println("Data Profile:");
    for (ProfileMetric p : metrics.values()) {
      System.out.println(p.toString());
    }
  }

}
