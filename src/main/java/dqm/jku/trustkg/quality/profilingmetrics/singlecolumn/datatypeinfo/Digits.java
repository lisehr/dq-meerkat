package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberValueUtils;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Digits")
public class Digits extends DependentProfileMetric {
  public Digits() {

  }

  public Digits(DataProfile d) {
    super(dig, d);
  }

  /**
   * Local variant of calculation to prevent a double check for dependent metrics
   * 
   * @param rl      the recordlist
   * @param oldVal  old value of metric
   * @param checked flag for dependency check
   */
  private void calculation(RecordList rl, Object oldVal, boolean checked) {
    Attribute a = (Attribute) super.getRefElem();
    super.setValueClass(Integer.class);
    if (a.getDataType() == Object.class) return;
    if (a.getDataType() == String.class) {
      super.setValue(0);
      return;
    }
    if (!checked) this.dependencyCalculationWithRecordList(rl);
    Number maxNum = (Number) super.getRefProf().getMetric(max).getValue();
    Number minNum = (Number) super.getRefProf().getMetric(min).getValue();
    if (maxNum == null && minNum == null) {
      super.setValue(null);
      return;
    }
    int maxDigs = 0;
    int minDigs = 0;
    if (maxNum != null) {
      maxDigs = NumberValueUtils.countDigits(maxNum);
      if (minNum == null) {
        super.setValue(maxDigs);
        return;
      }
    }
    if (minNum != null) {
      minDigs = NumberValueUtils.countDigits(minNum);
      if (maxNum == null) {
        super.setValue(minDigs);
        return;
      }
    }
    if (maxDigs > minDigs) super.setValue(maxDigs);
    else super.setValue(minDigs);
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
    calculation(rs, null);
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
    if (super.getMetricPos(dig) - 1 <= super.getMetricPos(max)) super.getRefProf().getMetric(max).calculationNumeric(list, null);
    if (super.getMetricPos(dig) - 2 <= super.getMetricPos(min)) super.getRefProf().getMetric(min).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCalculationWithRecordList(RecordList rl) {
    if (super.getMetricPos(dig) - 1 <= super.getMetricPos(max)) super.getRefProf().getMetric(max).calculation(rl, null);
    if (super.getMetricPos(dig) - 2 <= super.getMetricPos(min)) super.getRefProf().getMetric(min).calculation(rl, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric maxM = super.getRefProf().getMetric(max);
    if (maxM == null) {
      maxM = new Maximum(super.getRefProf());
      super.getRefProf().addMetric(maxM);
    }
    ProfileMetric minM = super.getRefProf().getMetric(min);
    if (minM == null) {
      minM = new Minimum(super.getRefProf());
      super.getRefProf().addMetric(minM);
    }
  }

}
