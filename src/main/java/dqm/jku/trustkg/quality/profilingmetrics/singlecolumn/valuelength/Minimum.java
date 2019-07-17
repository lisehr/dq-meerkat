package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.List;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

public class Minimum extends ProfileMetric {
  private static final String name = "Minimum";

  public Minimum(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    Object val = null;
    if (oldVal == null) val = getBasicInstance();
    else val = oldVal;
    for (Record r : rs) {
      Object field = r.getField(a);
      val = getMinimum(val, field);
    }
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  /**
   * Creates a basic instance used as a reference (in this case the maximum value)
   * 
   * @return the reference value
   */
  private Object getBasicInstance() {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MAX_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MAX_VALUE);
    else return Integer.MAX_VALUE;
  }

  /**
   * Checks the minimum value of two objects
   * 
   * @param current the current minimum value
   * @param toComp  the new value to compare
   * @return the new minimum value
   */
  private Object getMinimum(Object current, Object toComp) {
    if (toComp == null) return current;
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.min((long) current, ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.min((double) current, ((Number) toComp).doubleValue());
    else return Integer.min((int) current, ((String) toComp).length());
  }

  @Override
  public void update(RecordSet rs) {
    calculation(rs, super.getValue());
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    if (list == null || list.isEmpty()) return;
    list.sort(new NumberComparator());
    Attribute a = (Attribute) super.getRefElem();
    Object val = null;
    if (oldVal == null) val = list.get(0);
    else val = getMinimum(list.get(0), oldVal);
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  @Override
  protected String getValueString() {
    return "\t" + super.getValue().toString();
  }

}
