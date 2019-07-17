package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.ArrayList;
import java.util.List;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

public class Median extends ProfileMetric {
  private static final String name = "Median";
  private List<Number> values = new ArrayList<Number>();

  public Median(DataProfile d) {
    super(name, d);
  }

  @Override
  public void calculation(RecordSet rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    // TODO: unsure how median via old value can be calculated, is storing the list a optimal way of doing so???
    List<Number> list = new ArrayList<Number>();
    if (oldVal != null) list.addAll(values);
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    list.sort(new NumberComparator());
    Object val = getMedian(list, rs.size() + values.size());
    values.clear();
    values.addAll(list);
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  /**
   * Method for getting the median of a list of data
   * 
   * @param list the list of data (sorted in ascending order)
   * @param size the size of records
   * @return the median of the list
   */
  private Object getMedian(List<Number> list, int size) {
    boolean isEven = false;
    if (list.size() < size) return null;
    if (size % 2 == 0) isEven = true;
    size /= 2;
    Number val = list.get(size);
    if (isEven) val = averageResult(val, list.get(size + 1));
    return val;
  }

  /**
   * Method for averaging the result, used if the number of measured records is
   * even
   * 
   * @param oddMedian the median value if the amount of records is odd
   * @param next      the follow up value
   * @return the weighted median
   */
  private Number averageResult(Number oddMedian, Number next) {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return ((oddMedian.longValue() + oddMedian.longValue()) / 2);
    else if (a.getDataType().equals(Double.class)) return ((oddMedian.doubleValue() + oddMedian.doubleValue()) / 2);
    return oddMedian;
  }

  @Override
  public void update(RecordSet rs) {
    calculation(rs, super.getValue());
  }
  
  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    if (list == null || list.isEmpty()) return;
    list.sort(new NumberComparator());
    Attribute a = (Attribute) super.getRefElem();
    Object val = getMedian(list, list.size() + values.size());
    values.clear();
    values.addAll(list);
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  @Override
  protected String getValueString() {
    return "\t" + super.getValue().toString();
  }


}
