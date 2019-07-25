package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

@RDFNamespaces({ 
  "foaf = http://xmlns.com/foaf/0.1/",
})
@RDFBean("foaf:Minimum")
public class Minimum extends ProfileMetric {
  private static final String name = "Minimum";

  public Minimum() {
    
  }
  
  public Minimum(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    Object val = null;
    if (oldVal == null) val = getBasicInstance();
    else val = oldVal;
    for (Record r : rs) {
      Object field = r.getField(a);
      val = getMinimum(val, field);
    }
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  /**
   * Creates a basic instance used as a reference (in this case the maximum value)
   * 
   * @return the reference value
   */
  private Object getBasicInstance() {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MAX_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MAX_VALUE);
    else return Integer.MAX_VALUE;
  }

  /**
   * Checks the minimum value of two objects
   * 
   * @param current the current minimum value
   * @param toComp  the new value to compare
   * @return the new minimum value
   */
  private Object getMinimum(Object current, Object toComp) {
    if (toComp == null) return current;
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.min(((Number)current).longValue(), ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.min(((Number)current).doubleValue(), ((Number) toComp).doubleValue());
    else return Integer.min(((Number)current).intValue(), ((Number)toComp).intValue());
  }

  @Override
  public void update(RecordSet rs) {
    calculation(rs, super.getValue());
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    if (list == null || list.isEmpty()) {
      if (oldVal != null) return;
      else this.setValue(null);
    } else {
      list.sort(new NumberComparator());
      Object val = null;
      if (oldVal == null) val = getMinimum(list.get(0), getBasicInstance());
      else val = getMinimum(list.get(0), oldVal);
      this.setValue(val);
    }
    Attribute a = (Attribute) super.getRefElem();
    this.setValueClass(a.getDataType());
  }

  @Override
  protected String getValueString() {
    if (super.getValue() == null) return "\tnull";
    else return "\t" + super.getValue().toString();
  }

}
