package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

import java.util.List;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class NullValuesPercentage extends ProfileMetric {
  public NullValuesPercentage() {
    
  }
  
  public NullValuesPercentage(DataProfile d) {
    super(nullValP, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    long nominator = (long)super.getRefProf().getMetric(nullVal).getValue();
    int denominator = (int)super.getRefProf().getMetric(size).getValue();
    double result;
    if (denominator == 0) result = Double.valueOf(0);
    result = (double) nominator * 100.0 / (double) denominator;
    super.setValue(result);
    this.setValueClass(Double.class);
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
    if (getValue() == null) return "\tnull";
    else return "\t" + getValue().toString() + "%";
  }

}
