package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.List;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

public class Average extends ProfileMetric {
  private static final String name = "Average";

  public Average(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    Object val = null;
    if (oldVal == null) val = getBasicInstance();
    else val = oldVal;
    for (Record r : rs) {
      Object field = r.getField((Attribute) super.getRefElem());
      val = addValue(val, field);
    }
    val = performAveraging(val);
    this.setValue(val);
    this.setValueClass(((Attribute) super.getRefElem()).getDataType());
  }

  /**
   * Method for getting the average value of the objects
   * @param sum the sum of values 
   * @return the average value
   */
  private Object performAveraging(Object sum) {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return (long) sum / super.getRefProf().getRecordsProcessed();
    else if (a.getDataType().equals(Double.class)) return (double) sum / super.getRefProf().getRecordsProcessed();
    return (int) sum / super.getRefProf().getRecordsProcessed();
  }

  /**
   * Creates a basic instance used as a reference (in this case zero as a number)
   * @return the reference value
   */
  private Object getBasicInstance() {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.valueOf(0);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(0);
    else
      return Integer.valueOf(0);
  }

  /**
   * Adds a value to the sum of values
   * @param current the current sum of values
   * @param toAdd the value to be added
   * @return the new sum of values
   */
  private Object addValue(Object current, Object toAdd) {
    if (toAdd == null) return current;
    Attribute a = (Attribute) super.getRefElem();    
    if (a.getDataType().equals(Long.class)) return (long) current + ((Number) toAdd).longValue();
    else if (a.getDataType().equals(Double.class)) return (double) current + ((Number) toAdd).doubleValue();
    else if (toAdd.getClass().equals(String.class)) return (int) current + ((String) toAdd).length();
    else return (int) current + (int) toAdd;
  }

  @Override
  public void update(RecordSet rs) {
    calculation(rs, getOriginalSum());
  }

  /**
   * Restores the original sum of the averaging value
   * @return the sum before weighting with the amount of values
   */
  private Object getOriginalSum() {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return ((Number) super.getValue()).longValue() * super.getRefProf().getRecordsProcessed();
    else if (a.getDataType().equals(Double.class)) return ((Number) super.getValue()).doubleValue() * super.getRefProf().getRecordsProcessed();
    else
      return ((int) super.getValue()) * super.getRefProf().getRecordsProcessed();
  }
  
  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    if (list == null || list.isEmpty()) return;
    list.sort(new NumberComparator());
    Attribute a = (Attribute) super.getRefElem();
    Object sum = null;
    if (oldVal == null) sum = getBasicInstance();
    else sum = getOriginalSum();
    for (Number n : list) {
      sum = addValue(sum, n);
    }
    sum = performAveraging(sum);
    this.setValue(sum);
    this.setValueClass(a.getDataType());
  }
  
  @Override
  protected String getValueString() {
    return "\t" + super.getValue().toString();
  }


}
