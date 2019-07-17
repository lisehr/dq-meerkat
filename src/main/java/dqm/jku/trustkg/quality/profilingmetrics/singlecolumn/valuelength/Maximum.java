package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.List;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

public class Maximum extends ProfileMetric {
  private static final String name = "Maximum";

  public Maximum(DataProfile d) {
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
      val = getMaximum(val, field);
    }
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  /**
   * Creates a basic instance used as a reference (in this case the minimum value)
   * 
   * @return the reference value
   */
  private Object getBasicInstance() {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MIN_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MIN_VALUE);
    else return Integer.MIN_VALUE;
  }

  /**
   * Checks the maximum value of two objects
   * 
   * @param current the current maximum value
   * @param toComp  the new value to compare
   * @return the new maximum value
   */
  private Object getMaximum(Object current, Object toComp) {
    if (toComp == null) return current;
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.max((long) current, ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.max((double) current, ((Number) toComp).doubleValue());
    else return Integer.max((int) current, ((String) toComp).length());
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
    if (oldVal == null) val = list.get(list.size() - 1);
    else val = getMaximum(list.get(list.size() - 1), oldVal);
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }
  
  @Override
  protected String getValueString() {
    return "\t" + super.getValue().toString();
  }


}
