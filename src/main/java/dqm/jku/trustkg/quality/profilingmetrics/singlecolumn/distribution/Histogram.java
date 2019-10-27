package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.distribution;

import java.util.ArrayList;
import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberComparator;
import dqm.jku.trustkg.util.numericvals.ValueDistributionUtils;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Histogram")
public class Histogram extends ProfileMetric {
  private static final String name = "Histogram";
  private Number min;
  private Number max;
  private Number classrange;

  public Histogram() {

  }

  public Histogram(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
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
  private void processList(List<Number> list, SerializableMap vals) {
    list.sort(new NumberComparator());
    if (min == null) min = list.get(0).doubleValue();
    else min = Math.min(min.doubleValue(), list.get(0).doubleValue());
    if (max == null) max = list.get(list.size() - 1).doubleValue();
    else max = Math.max(max.doubleValue(), list.get(list.size() - 1).doubleValue());
    int k = ValueDistributionUtils.calculateNumberClasses((int)super.getRefProf().getMetric("Size").getValue());
    classrange = (max.doubleValue() - min.doubleValue()) / k;
    int classVals[];
    if (vals == null) classVals = new int[k];
    else classVals = constructArray();
    for (Number n : list) {
      if (n.doubleValue() == max.doubleValue()) classVals[k - 1]++;
      else classVals[(int) Math.floor((n.doubleValue() - min.doubleValue()) / classrange.doubleValue())]++;
    }
    SerializableMap classes = new SerializableMap();
    for (int i = 0; i < k; i++)
      classes.put(i, classVals[i]);
    this.setValue(classes);
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
    processList(list, (SerializableMap) super.getValue());
  }

  /**
   * Generates an array to handle the Map easier
   * 
   * @return frequency array
   */
  private int[] constructArray() {
    if (super.getValue() == null) throw new IllegalStateException("Map has to exist here!");
    int k = ValueDistributionUtils.calculateNumberClasses((int) super.getRefProf().getMetric("Size").getValue());
    int classes[] = new int[k];
    int j = 0;
    for (Integer i : ((SerializableMap) super.getValue()).values()) {
      classes[j] = i;
      j++;
    }
    return classes;
  }

  @Override
  protected String getValueString() {
    if (super.getValue() == null) return "\tnull";
    StringBuilder sb = new StringBuilder().append("\tNumber of classes: ");
    int k = ValueDistributionUtils.calculateNumberClasses((int)super.getRefProf().getMetric("Size").getValue());
    sb.append(k);
    sb.append(", ClassRange: ");
    sb.append(classrange);
    sb.append(", Values: ");
    for (Integer i : ((SerializableMap) super.getValue()).values()) {
      sb.append(i);
      sb.append("-");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  /**
   * Gets the minimum
   * 
   * @return the min
   */
  @RDF("foaf:min")
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
  @RDF("foaf:max")
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
  @RDF("foaf:classrange")
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

}
