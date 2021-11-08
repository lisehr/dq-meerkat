package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

import java.util.List;

import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;


/**
 * Describes the metric Uniqueness, the Cardinality in relation to the number of
 * rows.
 * The result is a percentage value between 0 and 100.
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/cardinality/Uniqueness")
public class Uniqueness extends DependentProfileStatistic {
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
    long cardinality = (long) (super.getRefProf().getStatistic(card).getValue());
    long numRecs = (long) super.getRefProf().getStatistic(numrows).getValue();
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
    if (super.getMetricPos(unique) - 2 <= super.getMetricPos(numrows)) super.getRefProf().getStatistic(numrows).calculation(rl, null);
    if (super.getMetricPos(unique) - 1 <= super.getMetricPos(card)) super.getRefProf().getStatistic(card).calculation(rl, null);

  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
    if (super.getMetricPos(unique) - 2 <= super.getMetricPos(numrows)) super.getRefProf().getStatistic(numrows).calculationNumeric(list, null);
    if (super.getMetricPos(unique) - 1 <= super.getMetricPos(card)) super.getRefProf().getStatistic(card).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileStatistic sizeM = super.getRefProf().getStatistic(numrows);
    if (sizeM == null) {
      sizeM = new NumRows(super.getRefProf());
      super.getRefProf().addStatistic(sizeM);
    }
    ProfileStatistic cardM = super.getRefProf().getStatistic(card);
    if (cardM == null) {
      cardM = new Cardinality(super.getRefProf());
      super.getRefProf().addStatistic(cardM);
    }
  }

	@Override
	public boolean checkConformance(ProfileStatistic m, double threshold) {
		// Excluded: depends on cardinality & num rows (RDP size != DP size)
		return true;
	}
}
