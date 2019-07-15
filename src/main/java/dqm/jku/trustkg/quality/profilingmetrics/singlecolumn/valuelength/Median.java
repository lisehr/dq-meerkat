package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class Median extends ProfileMetric {
  private static final String name = "Median";

  public Median() {
    super(name);
  }

  @Override
  public void calculation(RecordSet rs, Attribute a) {
    List<Number> list = new ArrayList<Number>();
    for (Record r : rs) {
      Number field = (Number) r.getField(a);
      list.add(field);
    }
    Object val = getMedian(a, list, rs.size());
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  private Object getMedian(Attribute a, List<Number> list, int size) {
    boolean isEven = false;
    if (list.size() < size) return null;
    if (size % 2 == 0) isEven = true;
    Number val = list.get(size);    
    if (isEven) val = averageResult(a, val, list.get(size + 1));
    return val;
  }

  private Number averageResult(Attribute a, Number val, Number next) {
    if (a.getDataType().equals(Integer.class)) return (double)((val.intValue() + val.intValue())/2);
    else if (a.getDataType().equals(Long.class)) return (double)((val.longValue() + val.longValue())/2);
    else if (a.getDataType().equals(Double.class)) return (double)((val.doubleValue() + val.doubleValue())/2);
    return val;
  }

}
