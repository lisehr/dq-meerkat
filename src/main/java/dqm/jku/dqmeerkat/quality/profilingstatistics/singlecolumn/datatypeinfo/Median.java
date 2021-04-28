package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

import java.util.ArrayList;
import java.util.List;

import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;


/**
 * Describes the metric Median, which is the middle value of the sorted values
 * of the Attribute. Does not always have to be the average value.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Median")
public class Median extends ProfileStatistic {
  public Median() {

  }

  public Median(DataProfile d) {
    super(med, dti, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    List<Number> list = new ArrayList<Number>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    list.sort(new NumberComparator());
    Object val = getMedian(list, list.size());
    this.setValue(val);
    this.setNumericVal(((Number) val).doubleValue());
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
    if (isEven) {
      if (size == 1) val = averageResult(val, list.get(0));
      else val = averageResult(val, list.get(size + 1));
    }
    if (((Attribute) super.getRefElem()).getDataType().equals(Long.class)) return val.longValue();
    else if (((Attribute) super.getRefElem()).getDataType().equals(Double.class)) return val.doubleValue();
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
  public void update(RecordList rs) {
    calculation(rs, super.getValue());
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    if (list == null || list.isEmpty()) {
      if (oldVal != null) return;
      else this.setValue(null);
    } else {
      list.sort(new NumberComparator());
      Object val = getMedian(list, list.size());
      this.setValue(val);
      this.setNumericVal(((Number) val).doubleValue());
    }
    Attribute a = (Attribute) super.getRefElem();
    this.setValueClass(a.getDataType());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

	@Override
	public boolean checkConformance(ProfileStatistic m, double threshold) {
		double rdpVal = ((Number) this.getNumericVal()).doubleValue();
		double dpValue = ((Number) m.getValue()).doubleValue();
		
		double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
		double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);
		
		boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
		if(!conf && Constants.DEBUG) System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
		return conf;
	}
}
