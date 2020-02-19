package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Size")
public class Size extends ProfileMetric {
  public Size() {
    
  }
  
  public Size(DataProfile d) {
    super(size, d);
  }


  @Override
  public void calculation(RecordList rs, Object oldVal) {
    super.setValue(rs.size());
    super.setValueClass(Integer.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    super.setValue(list.size());
    super.setValueClass(Integer.class);
  }

  @Override
  public void update(RecordList rs) {
    int oldSize = (int) super.getValue();
    super.setValue(oldSize + rs.size());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }
}
