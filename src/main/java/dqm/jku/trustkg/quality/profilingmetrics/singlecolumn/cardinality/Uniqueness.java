package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Uniqueness")
public class Uniqueness extends DependentProfileMetric{
  public Uniqueness() {
    
  }
  
  public Uniqueness(DataProfile d) {
    super(unique, d);
  }

  /**
   * Local variant of calculation to prevent a double check for dependent metrics
   * @param rl the recordlist
   * @param oldVal old value of metric
   * @param checked flag for dependency check
   */
  private void calculation(RecordList rl, Object oldVal, boolean checked) {
    if (!checked) dependencyCalculationWithRecordList(rl);
    long cardinality = (long)(super.getRefProf().getMetric(card).getValue());
    int numRecs = (int) super.getRefProf().getMetric(size).getValue();
    double result = cardinality * 100.0 / numRecs;
    this.setValue(result);
    this.setValueClass(Double.class);
    
  }
  
  @Override
  public void calculation(RecordList rs, Object oldVal) {
    calculation(rs, null, false);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
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
    if (super.getMetricPos(unique) - 1 <= super.getMetricPos(size)) super.getRefProf().getMetric(size).calculation(rl, null);
    if (super.getMetricPos(unique) - 2 <= super.getMetricPos(card)) super.getRefProf().getMetric(card).calculation(rl, null);
    
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) {
    if (super.getMetricPos(unique) - 1 <= super.getMetricPos(size)) super.getRefProf().getMetric(size).calculationNumeric(list, null);
    if (super.getMetricPos(unique) - 2 <= super.getMetricPos(card)) super.getRefProf().getMetric(card).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric sizeM = super.getRefProf().getMetric(size);
    if (sizeM == null) {
      sizeM = new Size(super.getRefProf());
      super.getRefProf().addMetric(sizeM);
    }
    ProfileMetric cardM = super.getRefProf().getMetric(card);
    if (cardM == null) {
      cardM = new Cardinality(super.getRefProf());
      super.getRefProf().addMetric(cardM);
    }
  }

}
