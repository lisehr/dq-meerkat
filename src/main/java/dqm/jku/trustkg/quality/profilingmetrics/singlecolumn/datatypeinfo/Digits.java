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

/**
 * Describes the metric Digits, which is the amount of digits before the decimal
 * point.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Digits")
public class Digits extends ProfileMetric {
  public Digits() {

  }

  public Digits(DataProfile d) {
    super(dig, d);
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
      return;
    }
    int digits = 0;
    for (Record r : rs) {
      int dig = calculateDigits(r.getField(a));
      if (dig > digits) digits = dig;
    }
    super.setValue(digits);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    Attribute a = (Attribute) super.getRefElem();
    super.setValueClass(Integer.class);
    if (a.getDataType() == Object.class) return;
    if (a.getDataType() == String.class) {
      super.setValue(0);
      return;
    }
    int digits = 0;
    for (Number n : list) {
      int dig = calculateDigits(n);
      if (dig > digits) digits = dig;
    }
    super.setValue(digits);
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
