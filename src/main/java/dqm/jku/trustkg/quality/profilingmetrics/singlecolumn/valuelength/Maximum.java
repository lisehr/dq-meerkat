package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class Maximum extends ProfileMetric {
  private static final String name = "Maximum";

  public Maximum() {
    super(name);
  }

  @Override
  public void calculation(RecordSet rs, Attribute a) {
    Object val = getBasicInstance(a);
    for (Record r : rs) {
      Object field = r.getField(a);
      val = getMaximum(a, val, field);
    }
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  /**
   * Creates a basic instance used as a reference (in this case the minimum value)
   * @param a the attribute used for determine the class
   * @return the reference value
   */
  private Object getBasicInstance(Attribute a) {
    if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MIN_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MIN_VALUE);
    else
      return Integer.MIN_VALUE;
  }

  /**
   * Checks the maximum value of two objects
   * @param a the attribute to be checked
   * @param current the current maximum value
   * @param toComp the new value to compare
   * @return the new maximum value
   */
  private Object getMaximum(Attribute a, Object current, Object toComp) {
    if (toComp == null) return current;
    if (a.getDataType().equals(Long.class)) return Long.max((long) current, ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.max((double) current, ((Number) toComp).doubleValue());
    else
      return Integer.max((int) current, ((String) toComp).length());
  }

}
