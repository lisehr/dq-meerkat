package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class Cardinality extends ProfileMetric {
  private static final String name = "Cardinality";

  public Cardinality() {
    
  }
  
  public Cardinality(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    Set<Number> set = new TreeSet<Number>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) set.add(field);
    }
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    Set<Number> set = new TreeSet<Number>();
    set.addAll(list);
    this.setValue((long) set.size());
    this.setValueClass(Long.class);
  }

  @Override
  public void update(RecordSet rs) {
    calculation(rs, super.getValue());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

}
