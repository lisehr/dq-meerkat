package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:NullValues")
public class NullValues extends ProfileMetric {
  public NullValues() {

  }

  public NullValues(DataProfile d) {
    super(nullVal, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    this.dependencyCalculationWithRecordList(rs);
    Attribute a = (Attribute) super.getRefElem();
    long nullVals = 0;
    for (Record r : rs) {
      if (r.getField(a) == null) nullVals++;
    }
    this.setValue(nullVals);
    this.setValueClass(Long.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    this.dependencyCalculationWithNumericList(list);
    int siz = (int) super.getRefProf().getMetric(size).getValue();
    long nullVals = list.size() - siz;
    this.setValue(nullVals);
    this.setValueClass(Long.class);
  }

  @Override
  public void update(RecordList rs) {
    calculation(rs, super.getValue());
  }

  @Override
  protected String getValueString() {
    if (getValue() == null) return "\tnull";
    return "\t" + getValue().toString();
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) {
    if (super.getMetricPos(nullVal) - 1 <= super.getMetricPos(size)) super.getRefProf().getMetric(size).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCalculationWithRecordList(RecordList rl) {
    if (super.getMetricPos(nullVal) - 1 <= super.getMetricPos(size)) super.getRefProf().getMetric(size).calculation(rl, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric sizeM = super.getRefProf().getMetric(size);
    if (sizeM == null) {
      sizeM = new Size(super.getRefProf());
      super.getRefProf().addMetric(sizeM);
    }
  }

}
