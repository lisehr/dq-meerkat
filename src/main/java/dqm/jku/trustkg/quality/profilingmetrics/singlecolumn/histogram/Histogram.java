package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.histogram;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.hist;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.numrows;

import java.util.ArrayList;
import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.NumRows;
import dqm.jku.trustkg.util.numericvals.NumberComparator;
import dqm.jku.trustkg.util.numericvals.ValueDistributionUtils;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.*;


/**
 * Describes the metric Histogram, which is a value distribution (equi-width).
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/histogram/Histogram")
public class Histogram extends DependentProfileMetric {
  private Number min; // minimum value
  private Number max; // maximum value
  private Number classrange; // range of equi-width classes

  public Histogram() {

  }

  public Histogram(DataProfile d) {
    super(hist, histCat, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    this.dependencyCalculationWithRecordList(rs);
    Attribute a = (Attribute) super.getRefElem();
    List<Number> list = new ArrayList<Number>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = r.getField(a).toString().length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    processList(list, null);
    this.setValueClass(a.getDataType());
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    this.dependencyCalculationWithNumericList(list);
    if (list == null || list.isEmpty()) {
      if (oldVal != null) return;
      else this.setValue(null);
    } else processList(list, null);
    Attribute a = (Attribute) super.getRefElem();
    this.setValueClass(a.getDataType());
  }

  /**
   * Helping method for creating histogram from a list
   * 
   * @param list the list to be processed
   */
  private void processList(List<Number> list, SerializableFrequencyMap vals) {
    if (list.isEmpty()) {
      this.setValue(null);
      return;
    }
    list.sort(new NumberComparator());
    if (min == null) min = list.get(0).doubleValue();
    else min = Math.min(min.doubleValue(), list.get(0).doubleValue());
    if (max == null) max = list.get(list.size() - 1).doubleValue();
    else max = Math.max(max.doubleValue(), list.get(list.size() - 1).doubleValue());
    int k = ValueDistributionUtils.calculateNumberClasses((int) super.getRefProf().getMetric(numrows).getValue());
    classrange = (max.doubleValue() - min.doubleValue()) / k;
    int classVals[];
    if (vals == null) classVals = new int[k];
    else classVals = constructArray();
    for (Number n : list) {
      if (n.doubleValue() == max.doubleValue()) classVals[k - 1]++;
      else classVals[(int) Math.floor((n.doubleValue() - min.doubleValue()) / classrange.doubleValue())]++;
    }
    SerializableFrequencyMap classes = new SerializableFrequencyMap(this.getUri());
    for (int i = 0; i < k; i++) classes.put(i, classVals[i]);
    this.setValue(classes);
    this.setNumericVal(classes);
  }

  @Override
  public void update(RecordList rs) {
    Attribute a = (Attribute) super.getRefElem();
    List<Number> list = new ArrayList<Number>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    processList(list, (SerializableFrequencyMap) super.getValue());
  }

  /**
   * Generates an array to handle the Map easier
   * 
   * @return frequency array
   */
  private int[] constructArray() {
    if (super.getValue() == null) throw new IllegalStateException("Map has to exist here!");
    int k = getNumberOfClasses();
    int classes[] = new int[k];
    int j = 0;
    for (Integer i : ((SerializableFrequencyMap) super.getValue()).values()) {
      classes[j] = i;
      j++;
    }
    return classes;
  }

  @Override
  protected String getValueString() {
    if (super.getValue() == null) return "\tnull";
    StringBuilder sb = new StringBuilder().append("\tNumber of classes: ");
    int k = ValueDistributionUtils.calculateNumberClasses((int) super.getRefProf().getMetric(numrows).getValue());
    sb.append(k);
    sb.append(", ClassRange: ");
    sb.append(classrange);
    sb.append(", Values: ");
    sb.append(getClassValues());
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  /**
   * Gets the minimum
   * 
   * @return the min
   */
  @RDF("dsd:hasMin")
  public Number getMin() {
    return min;
  }

  /**
   * Sets the minimum (security threat but needed by rdfbeans)
   * 
   * @param min the min to set
   */
  public void setMin(Number min) {
    this.min = min;
  }

  /**
   * Gets the maximum
   * 
   * @return the max
   */
  @RDF("dsd:hasMax")
  public Number getMax() {
    return max;
  }

  /**
   * Sets the maximum (security threat but needed by rdfbeans)
   * 
   * @param max the max to set
   */
  public void setMax(Number max) {
    this.max = max;
  }

  /**
   * Gets the classrange
   * 
   * @return the classrange
   */
  @RDF("dsd:hasClassrange")
  public Number getClassrange() {
    return classrange;
  }

  /**
   * Sets the classrange (security threat but needed by rdfbeans)
   * 
   * @param classrange the classrange to set
   */
  public void setClassrange(Number classrange) {
    this.classrange = classrange;
  }

  /**
   * calculate the number of classes
   * 
   * @return number of classes
   */
  public int getNumberOfClasses() {
    return ValueDistributionUtils.calculateNumberClasses((int) super.getRefProf().getMetric(numrows).getValue());
  }

  /**
   * Get string representations of class bins
   * 
   * @return string of class bins
   */
  public String getClassValues() {
    StringBuilder sb = new StringBuilder();
    for (Integer i : ((SerializableFrequencyMap) super.getValue()).values()) {
      sb.append(i);
      sb.append("-");
    }
    return sb.toString();
  }

  /**
   * Get string representations of class bins in format for CSV files
   * 
   * @return string of class bins in format for CSV files
   */
  public String getClassValuesCSV() {
    if (super.getValue() == null) return "";
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (Integer i : ((SerializableFrequencyMap) super.getValue()).values()) {
      sb.append(i);
      sb.append(", ");
    }
    sb.delete(sb.length() - 2, sb.length());
    sb.append("]");
    return sb.toString();
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
    if (super.getMetricPos(hist) - 1 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCalculationWithRecordList(RecordList rl) {
    if (super.getMetricPos(hist) - 2 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculation(rl, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric sizeM = super.getRefProf().getMetric(numrows);
    if (sizeM == null) {
      sizeM = new NumRows(super.getRefProf());
      super.getRefProf().addMetric(sizeM);
    }
  }

@Override
public boolean checkConformance(ProfileMetric m, double threshold) {
	// TODO Auto-generated method stub
	return false;
}

}
