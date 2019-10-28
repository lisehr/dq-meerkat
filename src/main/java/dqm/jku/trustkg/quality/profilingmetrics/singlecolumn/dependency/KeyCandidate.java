package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.dependency;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:KeyCandidate")
public class KeyCandidate extends ProfileMetric {  
  public KeyCandidate() {
    
  }
  
  public KeyCandidate(DataProfile d) {
    super(keyCand, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    super.setValue(((long) super.getRefProf().getMetric(card).getValue()) == (int) super.getRefProf().getMetric(size).getValue()); 
    super.setValueClass(Boolean.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    calculation(null, null);
  }

  @Override
  public void update(RecordList rs) {
    calculation(null, null);
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

}
