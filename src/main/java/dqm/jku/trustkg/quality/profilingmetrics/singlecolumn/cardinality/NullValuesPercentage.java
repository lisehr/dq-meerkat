package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

import java.util.List;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class NullValuesPercentage extends DependentProfileMetric {
  public NullValuesPercentage() {

  }

  public NullValuesPercentage(DataProfile d) {
    super(nullValP, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    calculation(rs, oldVal, false);
  }
  
  /**
   * Local variant of calculation to prevent a double check for dependent metrics
   * @param rl the recordlist
   * @param oldVal old value of metric
   * @param checked flag for dependency check
   */
  private void calculation(RecordList rl, Object oldVal, boolean checked) {
    if (!checked) dependencyCalculationWithRecordList(rl);
    long nominator = (long) super.getRefProf().getMetric(nullVal).getValue();
    int denominator = (int) super.getRefProf().getMetric(size).getValue();
    double result;
    if (denominator == 0) result = Double.valueOf(0);
    result = (double) nominator * 100.0 / (double) denominator;
    super.setValue(result);
    this.setValueClass(Double.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    dependencyCalculationWithNumericList(list);
    calculation(null, null, true);
  }

  @Override
  public void update(RecordList rs) {
    calculation(rs, null);
  }

  @Override
  protected String getValueString() {
    if (getValue() == null) return "\tnull";
    else return "\t" + getValue().toString() + "%";
  }

  @Override
  protected void dependencyCalculationWithRecordList(RecordList rl) {
    if (super.getMetricPos(nullValP) - 1 <= super.getMetricPos(size)) super.getRefProf().getMetric(size).calculation(rl, null);
    if (super.getMetricPos(nullValP) - 2 <= super.getMetricPos(nullVal)) super.getRefProf().getMetric(nullVal).calculation(rl, null);
    
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) {
    if (super.getMetricPos(nullValP) - 1 <= super.getMetricPos(size)) super.getRefProf().getMetric(size).calculationNumeric(list, null);
    if (super.getMetricPos(nullValP) - 2 <= super.getMetricPos(nullVal)) super.getRefProf().getMetric(nullVal).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric sizeM = super.getRefProf().getMetric(size);
    if (sizeM == null) {
      sizeM = new Size(super.getRefProf());
      super.getRefProf().addMetric(sizeM);
    }
    ProfileMetric nullV = super.getRefProf().getMetric(nullVal);
    if (nullV == null) {
      nullV = new NullValues(super.getRefProf());
      super.getRefProf().addMetric(nullV);
    }
  }

}
