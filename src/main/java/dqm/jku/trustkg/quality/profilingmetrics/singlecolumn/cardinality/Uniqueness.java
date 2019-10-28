package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Uniqueness")
public class Uniqueness extends ProfileMetric{
  public Uniqueness() {
    
  }
  
  public Uniqueness(DataProfile d) {
    super(unique, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    long cardinality = (long)(super.getRefProf().getMetric(card).getValue());
    int numRecs = (int) super.getRefProf().getMetric(size).getValue();
    double result = cardinality * 100.0 / numRecs;
    this.setValue(result);
    this.setValueClass(Double.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    calculation(null, null); // in this case, no record set is needed, therefore null for rs is allowed
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

}
