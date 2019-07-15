package dqm.jku.trustkg.quality.profilingmetrics;

import java.util.Objects;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.RecordSet;

public abstract class ProfileMetric {
  private String label; // the naming of the metric
  private Class<?> valClass; // the class of the value
  private Object value; // the value itself
  
  public ProfileMetric(String label) {
    if (label == null) throw new IllegalArgumentException("Label cannot be null!");
    this.label = label;
    value = 0.0;
  }
  
  /**
   * Gets the label
   * @return label of metric
   */
  public String getLabel() {
    return label;
  }
  
  /**
   * Gets the value
   * @return value of metric
   */
  public Object getValue() {
    return value;
  }
  
  /**
   * Gets the class object of a value
   * @return class of value
   */
  public Class<?> getValueClass(){
    return valClass;
  }
  
  /**
   * Sets the class of the value
   * @param cls the new value class to be set
   */
  protected void setValueClass(Class<?> cls) {
    this.valClass = cls;
  }
  
  /**
   * Sets the value of the metric
   * @param value the new value to be set
   */
  protected void setValue(Object value) {
    this.value = value;
  }
  
  /**
   * Method for calculating the profile metric, overridden by each metric
   * @param rs the recordset used for calculation
   * @param a the attribute of the metric
   */
  public abstract void calculation(RecordSet rs, Attribute a);
  
  @Override
  public String toString() {
    if (value == null) return String.format("%s\t%s\tnull", label, valClass.getSimpleName());
    else return String.format("%s\t%s\t%s", label, valClass.getSimpleName(), value.toString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, valClass, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ProfileMetric)) return false;
    ProfileMetric other = (ProfileMetric) obj;
    return Objects.equals(label, other.label) && Objects.equals(valClass, other.valClass) && Objects.equals(value, other.value);
  }
  
  
}
