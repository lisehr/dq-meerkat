package dqm.jku.trustkg.quality.profilingmetrics;

import java.util.Objects;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.RecordSet;

public abstract class ProfileMetric {
  private String label;
  private Class<?> valClass;
  private Object value;
  
  public ProfileMetric(String label) {
    if (label == null) throw new IllegalArgumentException("Label cannot be null!");
    this.label = label;
    value = 0.0;
  }
  
  public String getLabel() {
    return label;
  }
  
  public Object getValue() {
    return value;
  }
  
  public Class<?> getValueClass(){
    return valClass;
  }
  
  protected void setValueClass(Class<?> cls) {
    this.valClass = cls;
  }
  
  protected void setValue(Object value) {
    this.value = value;
  }
  
  public abstract void calculation(RecordSet rs, Attribute a);
  
  public String toString() {
    return String.format("%s\t%s\t%s", label, valClass.toString(), value.toString());
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
