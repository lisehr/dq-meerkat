package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.dependency;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.Uniqueness;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

/**
 * Describes the metric of a key candidate. This is only the case, if the
 * Uniqueness is at 100%.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:KeyCandidate")
public class KeyCandidate extends DependentProfileMetric {
  public KeyCandidate() {

  }

  public KeyCandidate(DataProfile d) {
    super(keyCand, d);
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
    super.setValue(((double) this.getRefProf().getMetric(unique).getValue()) == (double) 1);
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
    if (super.getMetricPos(keyCand) - 1 <= super.getMetricPos(unique)) super.getRefProf().getMetric(unique).calculation(rl, null);
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
    if (super.getMetricPos(keyCand) - 1 <= super.getMetricPos(unique)) super.getRefProf().getMetric(unique).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric uniqueM = super.getRefProf().getMetric(unique);
    if (uniqueM == null) {
      uniqueM = new Uniqueness(super.getRefProf());
      super.getRefProf().addMetric(uniqueM);
    }
  }
}
