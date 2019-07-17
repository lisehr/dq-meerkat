package dqm.jku.trustkg.quality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength.*;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.distribution.*;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

public class DataProfile {
  private Map<String, ProfileMetric> metrics = new HashMap<>();
  private DSDElement elem;

  public DataProfile(RecordSet rs, DSDElement d) {
    this.elem = d;
    createStandardProfile(d);
    calculateInitialProfile(rs);
  }

  /**
   * calculates an initial data profile based on the values inserted in the
   * standard profile
   * 
   * @param rs the set of records used for calculation
   * @param d  the dsd element to be annotated (currently only Attribute for
   *           single column metrics)
   */
  private void calculateInitialProfile(RecordSet rs) {
    if (elem instanceof Attribute) calculateSingleColumn(rs);
  }

  private void calculateSingleColumn(RecordSet rs) {
    List<Number> l = createValueList(rs);
    for (ProfileMetric p : metrics.values()) {
      p.calculationNumeric(l, p.getValue());
    }
    
  }

  private List<Number> createValueList(RecordSet rs) {
    List<Number> list = new ArrayList<Number>();
    Attribute a = (Attribute) elem;
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    list.sort(new NumberComparator());
    return list;
  }

  /**
   * creates a standard data profile on which calculations can be made
   */
  private void createStandardProfile(DSDElement d) {
    if (d instanceof Attribute) {
      ProfileMetric min = new Minimum((Attribute) d);
      metrics.put(min.getLabel(), min);
      ProfileMetric max = new Maximum((Attribute) d);
      metrics.put(max.getLabel(), max);
      ProfileMetric avg = new Average((Attribute) d);
      metrics.put(avg.getLabel(), avg);
      ProfileMetric med = new Median((Attribute) d);
      metrics.put(med.getLabel(), med);
      ProfileMetric hist = new Histogram((Attribute) d);
      metrics.put(hist.getLabel(), hist);
    }
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
