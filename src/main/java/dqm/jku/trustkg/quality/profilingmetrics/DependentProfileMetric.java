package dqm.jku.trustkg.quality.profilingmetrics;

import java.util.List;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;

/**
 * Second hierarchy level of abstract base class for profile metrics, its main
 * use is to provide methods for including dependent metrics.
 * 
 * @author optimusseptim
 *
 */
public abstract class DependentProfileMetric extends ProfileMetric {

  public DependentProfileMetric() {

  }

  public DependentProfileMetric(MetricTitle title, DataProfile refProf) {
    super(title, refProf);
    this.dependencyCheck();
  }

  @Override
  public abstract void calculation(RecordList rs, Object oldVal);

  @Override
  public abstract void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException;

  @Override
  public abstract void update(RecordList rs);

  @Override
  protected abstract String getValueString();

  /**
   * Helper method to calculate missing dependencies in calculation with a list of
   * numbers as base. Can be empty in Metrics without a dependency.
   * 
   * @param list   the numeric value list
   * @param oldVal the old value
   * @throws NoSuchMethodException
   */
  protected abstract void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException;

  /**
   * Helper method to calculate missing dependencies in calculation with a
   * RecordList as base. Can be empty in Metrics without a dependency.
   * 
   * @param rl     the recordlist
   * @param oldVal the old value
   */
  protected abstract void dependencyCalculationWithRecordList(RecordList rl);

  /**
   * Helper method to add missing dependencies needed for metric calculation
   */
  protected abstract void dependencyCheck();
}
