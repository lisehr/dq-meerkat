package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberValueUtils;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Digits")
public class Digits extends ProfileMetric {
  public Digits() {
    
  }
  
  public Digits(DataProfile d) {
    super(dig, d);
  }


  @Override
  public void calculation(RecordList rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    super.setValueClass(Integer.class);

    if (a.getDataType() == String.class) {
      super.setValue(0);
      return;
    }
    Number maxNum = (Number) super.getRefProf().getMetric(max).getValue();
    Number minNum = (Number) super.getRefProf().getMetric(min).getValue();
    int maxDigs = NumberValueUtils.countDigits(maxNum);
    int minDigs = NumberValueUtils.countDigits(minNum);
    if (maxDigs > minDigs) super.setValue(maxDigs);
    else super.setValue(minDigs);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    calculation(null, null);
  }

  @Override
  public void update(RecordList rs) {
    calculation(rs, null);
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

}
