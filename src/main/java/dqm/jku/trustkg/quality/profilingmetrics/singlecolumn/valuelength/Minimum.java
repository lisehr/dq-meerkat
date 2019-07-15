package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class Minimum extends ProfileMetric {
  private static final String name = "Minimum";

  public Minimum() {
    super(name);
  }

  @Override
  public void calculation(RecordSet rs, Attribute a) {
    Object val = getBasicInstance(a);
    for (Record r : rs) {
      Object field = r.getField(a);
      val = getMinimum(a, val, field);
    }
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  /**
   * Creates a basic instance used as a reference (in this case the maximum value)
   * @param a the attribute used for determine the class
   * @return the reference value
   */
  private Object getBasicInstance(Attribute a) {
    if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MAX_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MAX_VALUE);
    else
      return Integer.MAX_VALUE;
  }
  
  /**
   * Checks the minimum value of two objects
   * @param a the attribute to be checked
   * @param current the current minimum value
   * @param toComp the new value to compare
   * @return the new minimum value
   */
  private Object getMinimum(Attribute a, Object current, Object toComp) {
    if (toComp == null) return current;
    if (a.getDataType().equals(Long.class)) return Long.min((long) current, ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.min((double) current, ((Number) toComp).doubleValue());
    else
      return Integer.min((int) current, ((String) toComp).length());
  }

}
