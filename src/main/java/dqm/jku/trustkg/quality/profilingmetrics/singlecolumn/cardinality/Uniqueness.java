package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.*;


/**
 * Describes the metric Uniqueness, the Cardinality in relation to the number of
 * rows.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/cardinality/Uniqueness")
public class Uniqueness extends DependentProfileMetric {
  public Uniqueness() {

  }

  public Uniqueness(DataProfile d) {
    super(unique, cardCat, d);
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
    long cardinality = (long) (super.getRefProf().getMetric(card).getValue());
    int numRecs = (int) super.getRefProf().getMetric(numrows).getValue();
    double result = cardinality * 100.0 / numRecs;
    this.setValue(result);
    this.setNumericVal((Number) result);
    this.setValueClass(Double.class);

  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    calculation(rs, null, false);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    this.dependencyCalculationWithNumericList(list);
    calculation(null, null, true); // in this case, no record set is needed, therefore null for rs is allowed
  }

  @Override
  public void update(RecordList rs) {
    calculation(rs, super.getValueClass());
  }

  @Override
  protected String getValueString() {
    if (getValue() == null) return "\tnull";
    else return "\t" + getValue().toString() + "%";
  }

  @Override
  protected void dependencyCalculationWithRecordList(RecordList rl) {
    if (super.getMetricPos(unique) - 2 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculation(rl, null);
    if (super.getMetricPos(unique) - 1 <= super.getMetricPos(card)) super.getRefProf().getMetric(card).calculation(rl, null);

  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
    if (super.getMetricPos(unique) - 2 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculationNumeric(list, null);
    if (super.getMetricPos(unique) - 1 <= super.getMetricPos(card)) super.getRefProf().getMetric(card).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric sizeM = super.getRefProf().getMetric(numrows);
    if (sizeM == null) {
      sizeM = new NumRows(super.getRefProf());
      super.getRefProf().addMetric(sizeM);
    }
    ProfileMetric cardM = super.getRefProf().getMetric(card);
    if (cardM == null) {
      cardM = new Cardinality(super.getRefProf());
      super.getRefProf().addMetric(cardM);
    }
  }

}
