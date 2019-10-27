package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class KeyCandidate extends ProfileMetric {
  private static final String name = "isCandidateKey";
  
  public KeyCandidate() {
    
  }
  
  public KeyCandidate(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    super.setValue(((long) super.getRefProf().getMetric("Cardinality").getValue()) == 1); 
    super.setValueClass(Boolean.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    calculation(null, null);
  }

  @Override
  public void update(RecordList rs) {
    calculation(null, null);
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

}
