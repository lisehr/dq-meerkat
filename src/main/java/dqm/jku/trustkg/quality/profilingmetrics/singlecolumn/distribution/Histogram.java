package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberComparator;
import dqm.jku.trustkg.util.numericvals.ValueDistributionUtils;

public class Histogram extends ProfileMetric{
  private static final String name = "Histogram";
  private int n;
  private Number min;
  private Number max;
  private int k;
  private Number classrange;

  public Histogram(DSDElement refElem) {
    super(name, refElem);
  }

  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    List<Number> list = new ArrayList<Number>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    processList(list, null);
    this.setValueClass(a.getDataType());
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    if (list == null || list.isEmpty()) return;
    Attribute a = (Attribute) super.getRefElem();
    processList(list, null);
    this.setValueClass(a.getDataType());
  }

  /**
   * Helping method for creating histogram from a list
   * @param list the list to be processed
   */
  private void processList(List<Number> list, Map<Integer, Integer> vals) {
    list.sort(new NumberComparator());
    n = list.size();
    if (min == null) min = list.get(0).doubleValue();
    else min = Math.min(min.doubleValue(), list.get(0).doubleValue());
    if (max == null) max = list.get(n - 1).doubleValue();
    else max = Math.max(max.doubleValue(), list.get(n - 1).doubleValue());
    k = ValueDistributionUtils.calculateNumberClasses(n);
    classrange = (max.doubleValue() - min.doubleValue()) / k;
    int classVals[];
    if (vals == null) classVals = new int[k];
    else classVals = constructArray();
    for (Number n : list) {
      if (n.doubleValue() == max.doubleValue()) classVals[k - 1]++;
      else classVals[(int) Math.floor((n.doubleValue() - min.doubleValue()) / classrange.doubleValue())]++;
    }
    Map<Integer, Integer> classes = new HashMap<>();
    for (int i = 0; i < k; i++) classes.put(i, classVals[i]);
    this.setValue(classes);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void update(RecordSet rs) {
    Attribute a = (Attribute) super.getRefElem();
    List<Number> list = new ArrayList<Number>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    processList(list, (Map<Integer, Integer>) super.getValue());
  }
  
  /**
   * Generates an array to handle the Map easier
   * @return frequency array
   */
  @SuppressWarnings("unchecked")
  private int[] constructArray() {
    if (super.getValue() == null) throw new IllegalStateException("Map has to exist here!");
    int classes[] = new int[k];
    int j = 0;
    for (Integer i : ((Map<Integer, Integer>)super.getValue()).values()) {
      classes[j] = i;
      j++;
    }
    return classes;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected String getValueString() {
    StringBuilder sb = new StringBuilder().append("Number of classes: ");
    sb.append(k);
    sb.append(", ClassRange: ");
    sb.append(classrange);
    sb.append(", Values: ");
    for (Integer i : ((Map<Integer, Integer>)super.getValue()).values()) {
      sb.append(i);
      sb.append("-");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }


}
