package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class Average extends ProfileMetric {
  private static final String name = "Average";

  public Average() {
    super(name);
  }

  @Override
  public void calculation(RecordSet rs, Attribute a) {
    Object val = getBasicInstance(a);
    for (Record r : rs) {
      Object field = r.getField(a);
      val = addValue(a, val, field);
    }
    val = performAveraging(a, val, rs.size());
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  /**
   * Method for getting the average value of the objects
   * @param a the attibute for getting the class
   * @param sum the sum of values 
   * @param size the amount of records inspected
   * @return the average value
   */
  private Object performAveraging(Attribute a, Object sum, int size) {
    if (a.getDataType().equals(Long.class)) return (long) sum / size;
    else if (a.getDataType().equals(Double.class)) return (double) sum / size;
    return (int) sum / size;
  }

  /**
   * Creates a basic instance used as a reference (in this case zero as a number)
   * @param a the attribute used for determine the class
   * @return the reference value
   */
  private Object getBasicInstance(Attribute a) {
    if (a.getDataType().equals(Long.class)) return Long.valueOf(0);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(0);
    else
      return Integer.valueOf(0);
  }

  /**
   * Adds a value to the sum of values
   * @param a the attribute for class checking
   * @param current the current sum of values
   * @param toAdd the value to be added
   * @return the new sum of values
   */
  private Object addValue(Attribute a, Object current, Object toAdd) {
    if (toAdd == null) return current;
    if (a.getDataType().equals(Long.class)) return (long) current + ((Number) toAdd).longValue();
    else if (a.getDataType().equals(Double.class)) return (double) current + ((Number) toAdd).doubleValue();
    else
      return (int) current + ((String) toAdd).length();
  }

}
