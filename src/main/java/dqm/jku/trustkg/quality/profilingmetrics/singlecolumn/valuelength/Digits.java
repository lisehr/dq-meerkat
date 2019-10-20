package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.List;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberValueUtils;

public class Digits extends ProfileMetric {
  private static final String name = "Digits";

  public Digits() {
    
  }
  
  public Digits(DataProfile d) {
    super(name, d);
  }


  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    super.setValueClass(Integer.class);

    if (a.getDataType() == String.class) {
      super.setValue(0);
      return;
    }
    Number max = (Number) super.getRefProf().getMetric("Maximum").getValue();
    Number min = (Number) super.getRefProf().getMetric("Minimum").getValue();
    int maxDigs = NumberValueUtils.countDigits(max);
    int minDigs = NumberValueUtils.countDigits(min);
    if (maxDigs > minDigs) super.setValue(maxDigs);
    else super.setValue(minDigs);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    calculation(null, null);
  }

  @Override
  public void update(RecordSet rs) {
    calculation(rs, null);
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

}
