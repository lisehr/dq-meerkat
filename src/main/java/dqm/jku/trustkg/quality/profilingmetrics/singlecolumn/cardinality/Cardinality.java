package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Cardinality")
public class Cardinality extends ProfileMetric {

  public Cardinality() {
    
  }
  
  public Cardinality(DataProfile d) {
    super(card, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    Set<Number> set = new HashSet<Number>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) set.add(field);
    }
    this.setValue((long) set.size());
    this.setValueClass(Long.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    Set<Number> set = new HashSet<Number>();
    set.addAll(list);
    this.setValue((long) set.size());
    this.setValueClass(Long.class);
  }

  @Override
  public void update(RecordList rs) {
    calculation(rs, super.getValue());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) {
  }

  @Override
  protected void dependencyCalculationWithRecordList(RecordList rl) {
  }

  @Override
  protected void dependencyCheck() {
  }

}
