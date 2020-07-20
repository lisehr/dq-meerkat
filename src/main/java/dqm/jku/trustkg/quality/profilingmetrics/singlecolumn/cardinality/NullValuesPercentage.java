package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

/**
 * Describes the metric Null Values Percentage, which is the amount of Null
 * Values in relation to the data set size.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:NullValuesPercentage")
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
   * 
   * @param rl      the recordlist
   * @param oldVal  old value of metric
   * @param checked flag for dependency check
   */
  private void calculation(RecordList rl, Object oldVal, boolean checked) {
    if (!checked) dependencyCalculationWithRecordList(rl);
    long nominator = (long) super.getRefProf().getMetric(nullVal).getValue();
    int denominator = (int) super.getRefProf().getMetric(numrows).getValue();
    double result;
    if (denominator == 0) result = Double.valueOf(0);
    result = (double) nominator * 100.0 / (double) denominator;
    super.setValue(result);
    this.setValueClass(Double.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
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
    if (super.getMetricPos(nullValP) - 2 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculation(rl, null);
    if (super.getMetricPos(nullValP) - 1 <= super.getMetricPos(nullVal)) super.getRefProf().getMetric(nullVal).calculation(rl, null);

  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
    if (super.getMetricPos(nullValP) - 2 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculationNumeric(list, null);
    if (super.getMetricPos(nullValP) - 1 <= super.getMetricPos(nullVal)) super.getRefProf().getMetric(nullVal).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric sizeM = super.getRefProf().getMetric(numrows);
    if (sizeM == null) {
      sizeM = new NumRows(super.getRefProf());
      super.getRefProf().addMetric(sizeM);
    }
    ProfileMetric nullV = super.getRefProf().getMetric(nullVal);
    if (nullV == null) {
      nullV = new NullValues(super.getRefProf());
      super.getRefProf().addMetric(nullV);
    }
  }

}
