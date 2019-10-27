package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class Size extends ProfileMetric {
  private static final String name = "Size";

  public Size() {
    
  }
  
  public Size(DataProfile d) {
    super(name, d);
  }


  @Override
  public void calculation(RecordList rs, Object oldVal) {
    super.setValue(rs.size());
    super.setValueClass(Integer.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    super.setValue(list.size());
    super.setValueClass(Integer.class);
  }

  @Override
  public void update(RecordList rs) {
    int oldSize = (int) super.getValue();
    super.setValue(oldSize + rs.size());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

}
