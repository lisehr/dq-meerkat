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

  private Object getBasicInstance(Attribute a) {
    if (a.getDataType().equals(Integer.class)) return Integer.valueOf(Integer.MIN_VALUE);
    else if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MIN_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MIN_VALUE);
    else
      return String.valueOf("");
  }

  private Object getMaximum(Attribute a, Object current, Object toComp) {
    if (a.getDataType().equals(Integer.class)) return Integer.max((int) current, ((Number) toComp).intValue());
    else if (a.getDataType().equals(Long.class)) return Long.max((long) current, ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.max((double) current, ((Number) toComp).doubleValue());
    else
      return (String) current;
  }

}
