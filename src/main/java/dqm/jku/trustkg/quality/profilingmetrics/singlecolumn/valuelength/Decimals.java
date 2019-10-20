package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.List;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

public class Decimals extends ProfileMetric {
  private static final String name = "Decimals";
  
  public Decimals() {
    
  }
  
  public Decimals(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    this.setValueClass(Integer.class);
    if (a.getDataType() == Integer.class || a.getDataType() == Long.class || a.getDataType() == String.class) {
      this.setValue(0);
      return;
    }
    
    int decimals;    
    if (oldVal == null) decimals = 0;
    else decimals = (int) oldVal;
    for (Record r : rs) {
      Object field = r.getField(a);
      decimals = getDecimals(decimals, (Number) field);
    }
    this.setValue(decimals);
  }

  private int getDecimals(int decimals, Number field) {
    String numStr = field.toString();
    int pointPos = numStr.indexOf('.');
    int dec = numStr.length() - pointPos - 1;
    if (dec > decimals) return dec;
    else return decimals;
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    Attribute a = (Attribute) super.getRefElem();
    this.setValueClass(Integer.class);
    if (a.getDataType() == Integer.class || a.getDataType() == Long.class || a.getDataType() == String.class) {
      this.setValue(0);
      return;
    }
    
    int decimals;    
    if (oldVal == null) decimals = 0;
    else decimals = (int) oldVal;
    for (Number n : list) {
      decimals = getDecimals(decimals, (Number) n);
    }
    this.setValue(decimals);
  }

  @Override
  public void update(RecordSet rs) {
    calculation(rs, super.getValue());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

}
