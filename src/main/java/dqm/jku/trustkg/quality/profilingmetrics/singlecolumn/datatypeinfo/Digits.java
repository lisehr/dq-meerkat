package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.*;


/**
 * Describes the metric Digits, which is the amount of digits before the decimal
 * point.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Digits")
public class Digits extends ProfileMetric {
  public Digits() {

  }

  public Digits(DataProfile d) {
    super(dig, dti, d);
  }

  private int calculateDigits(Object field) {
    String valueStr = String.valueOf(field);
    int value = valueStr.indexOf(".");
    if (value == -1) value = valueStr.length();
    if (valueStr.indexOf('-') != -1) value--;
    return value;
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    super.setValueClass(Integer.class);
    if (a.getDataType() == Object.class) return;
    if (a.getDataType() == String.class) {
      super.setValue(0);
      this.setNumericVal(((Number) 0).longValue());
      return;
    }
    int digits = 0;
    for (Record r : rs) {
      int dig = calculateDigits(r.getField(a));
      if (dig > digits) digits = dig;
    }
    super.setValue(digits);
    this.setNumericVal(((Number) digits).longValue());
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    Attribute a = (Attribute) super.getRefElem();
    super.setValueClass(Integer.class);
    if (a.getDataType() == Object.class) return;
    if (a.getDataType() == String.class) {
      super.setValue(0);
      this.setNumericVal(((Number) 0).longValue());
      return;
    }
    int digits = 0;
    for (Number n : list) {
      int dig = calculateDigits(n);
      if (dig > digits) digits = dig;
    }
    super.setValue(digits);
    this.setNumericVal(((Number) digits).longValue());
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
