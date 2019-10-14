package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;

import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class Uniqueness extends ProfileMetric{
  private static final String name = "Uniqueness";
  
  public Uniqueness() {
    
  }
  
  public Uniqueness(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    long cardinality = (long)(super.getRefProf().getMetric("Cardinality").getValue());
    int numRecs = super.getRefProf().getRecordsProcessed();
    double result = cardinality * 100.0 / numRecs;
    this.setValue(result);
    this.setValueClass(Double.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    calculation(null, null); // in this case, no record set is needed, therefore null for rs is allowed
  }

  @Override
  public void update(RecordSet rs) {
    calculation(rs, super.getValueClass());
  }

  @Override
  protected String getValueString() {
    if (getValue() == null) return "\tnull";
    else return "\t" + getValue().toString() + "%";
  }

}
