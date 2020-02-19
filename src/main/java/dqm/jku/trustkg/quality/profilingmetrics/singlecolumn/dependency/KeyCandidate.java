package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.dependency;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.Cardinality;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.Size;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:KeyCandidate")
public class KeyCandidate extends ProfileMetric {  
  public KeyCandidate() {
    
  }
  
  public KeyCandidate(DataProfile d) {
    super(keyCand, d);
  }

  /**
   * Local variant of calculation to prevent a double check for dependent metrics
   * @param rl the recordlist
   * @param oldVal old value of metric
   * @param checked flag for dependency check
   */
  private void calculation(RecordList rl, Object oldVal, boolean checked) {
    if (!checked) this.dependencyCalculationWithRecordList(rl);
    super.setValue(((long) super.getRefProf().getMetric(card).getValue()) == (int) super.getRefProf().getMetric(size).getValue()); 
    super.setValueClass(Boolean.class);
  }
  
  @Override
  public void calculation(RecordList rs, Object oldVal) {
    calculation(rs, null, false);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
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
    if (super.getMetricPos(keyCand) - 1 <= super.getMetricPos(size)) super.getRefProf().getMetric(size).calculation(rl, null);
    if (super.getMetricPos(keyCand) - 2 <= super.getMetricPos(card)) super.getRefProf().getMetric(card).calculation(rl, null);    
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) {
    if (super.getMetricPos(keyCand) - 1 <= super.getMetricPos(size)) super.getRefProf().getMetric(size).calculationNumeric(list, null);
    if (super.getMetricPos(keyCand) - 2 <= super.getMetricPos(card)) super.getRefProf().getMetric(card).calculationNumeric(list, null);
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
