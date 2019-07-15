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

  private Object performAveraging(Attribute a, Object val, int size) {
    if (a.getDataType().equals(Integer.class)) return (int) val / size;
    else if (a.getDataType().equals(Long.class)) return (long) val / size;
    else if (a.getDataType().equals(Double.class)) return (double) val / size;
    return val;
  }

  private Object getBasicInstance(Attribute a) {
    if (a.getDataType().equals(Integer.class)) return Integer.valueOf(0);
    else if (a.getDataType().equals(Long.class)) return Long.valueOf(0);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(0);
    else
      return String.valueOf("");
  }

  private Object addValue(Attribute a, Object current, Object toComp) {
    if (a.getDataType().equals(Integer.class)) return (int) current + ((Number) toComp).intValue();
    else if (a.getDataType().equals(Long.class)) return (long) current + ((Number) toComp).longValue();
    else if (a.getDataType().equals(Double.class)) return (double) current + ((Number) toComp).doubleValue();
    else
      return (String) current;
  }

}
