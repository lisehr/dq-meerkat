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

  private Object getBasicInstance(Attribute a) {
    if (a.getDataType().equals(Integer.class)) return Integer.valueOf(Integer.MAX_VALUE);
    else if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MAX_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MAX_VALUE);
    else
      return String.valueOf("");
  }

  private Object getMinimum(Attribute a, Object current, Object toComp) {
    if (a.getDataType().equals(Integer.class)) return Integer.min((int) current, ((Number) toComp).intValue());
    else if (a.getDataType().equals(Long.class)) return Long.min((long) current, ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.min((double) current, ((Number) toComp).doubleValue());
    else
      return (String) current;
  }

}
