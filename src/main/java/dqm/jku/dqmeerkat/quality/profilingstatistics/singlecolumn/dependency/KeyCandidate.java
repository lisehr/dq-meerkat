package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.dependency;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

import java.util.List;

import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.Uniqueness;
import dqm.jku.dqmeerkat.util.Constants;


/**
 * Describes the metric of a key candidate. This is only the case, if the
 * Uniqueness is at 100%.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dependency/KeyCandidate")
public class KeyCandidate extends DependentProfileStatistic {
  public KeyCandidate() {

  }

  public KeyCandidate(DataProfile d) {
    super(keyCand, depend, d);
  }

  /**
   * Local variant of calculation to prevent a double check for dependent metrics
   * 
   * @param rl      the recordlist
   * @param oldVal  old value of metric
   * @param checked flag for dependency check
   */
  private void calculation(RecordList rl, Object oldVal, boolean checked) {
    if (!checked) this.dependencyCalculationWithRecordList(rl);
    boolean isKeyCandidate = ((double) this.getRefProf().getStatistic(unique).getValue()) == (double) 100.0;
    super.setValue(isKeyCandidate);

    super.setNumericVal(isKeyCandidate ? 1 : 0);
    super.setValueClass(Boolean.class);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    calculation(rs, null, false);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    this.dependencyCalculationWithNumericList(list);
    calculation(null, null, true);
  }

  @Override
  public void update(RecordList rs) {
    calculation(null, null);
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

  @Override
  protected void dependencyCalculationWithRecordList(RecordList rl) {
    if (super.getMetricPos(keyCand) - 1 <= super.getMetricPos(unique)) super.getRefProf().getStatistic(unique).calculation(rl, null);
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
    if (super.getMetricPos(keyCand) - 1 <= super.getMetricPos(unique)) super.getRefProf().getStatistic(unique).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileStatistic uniqueM = super.getRefProf().getStatistic(unique);
    if (uniqueM == null) {
      uniqueM = new Uniqueness(super.getRefProf());
      super.getRefProf().addStatistic(uniqueM);
    }
  }

@Override
public boolean checkConformance(ProfileStatistic m, double threshold) {
	String rdpVal = this.getSimpleValueString();
	String dpValue = this.getSimpleValueString();
	
	boolean conf = rdpVal.equals(dpValue);
	if(!conf && Constants.DEBUG) System.out.println(this.getTitle() + " exceeded: " + dpValue + " != " + rdpVal);
	return conf;
}
}
