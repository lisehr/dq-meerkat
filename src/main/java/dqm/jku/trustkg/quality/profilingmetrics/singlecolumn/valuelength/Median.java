package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.ArrayList;
import java.util.List;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

public class Median extends ProfileMetric {
  private static final String name = "Median";

  public Median() {
    super(name);
  }

  @Override
  public void calculation(RecordSet rs, Attribute a) {
    List<Number> list = new ArrayList<Number>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    list.sort(new NumberComparator());
    Object val = getMedian(a, list, rs.size());
    this.setValue(val);
    this.setValueClass(a.getDataType());
  }

  /**
   * Method for getting the median of a list of data
   * @param a the attribute used for getting the average result (if amount of records is even)
   * @param list the list of data (sorted in ascending order)
   * @param size the size of records
   * @return the median of the list
   */
  private Object getMedian(Attribute a, List<Number> list, int size) {
    boolean isEven = false;
    if (list.size() < size) return null;
    if (size % 2 == 0) isEven = true;
    size /= 2;
    Number val = list.get(size);    
    if (isEven) val = averageResult(a, val, list.get(size + 1));
    return val;
  }

  /**
   * Method for averaging the result, used if the number of measured records is even
   * @param a the attribute used for determining the class
   * @param val the median value if the amount of records is odd
   * @param next the follow up value
   * @return the weighted median
   */
  private Number averageResult(Attribute a, Number val, Number next) {
    if (a.getDataType().equals(Long.class)) return((val.longValue() + val.longValue())/2);
    else if (a.getDataType().equals(Double.class)) return ((val.doubleValue() + val.doubleValue())/2);
    return val;
  }

}
