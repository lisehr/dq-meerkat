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

/**
 * Describes the metric Null Values, the amount of empty fields in a Attribute
 * 
 * @author optimusseptim
 *
 */
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
    Attribute a = (Attribute) super.getRefElem();
    long nullVals = 0;
    for (Record r : rs) {
      if (r.getField(a) == null) nullVals++;
    }
    this.setValue(nullVals);
    this.setValueClass(Long.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    throw new NoSuchMethodException("Method not allowed for numeric lists!");
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
}
