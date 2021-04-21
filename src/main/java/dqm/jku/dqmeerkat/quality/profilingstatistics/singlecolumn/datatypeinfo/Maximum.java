package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

import java.util.List;

import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;


/**
 * Describes the metric Maximum, which denotes the maximum value in an
 * Attribute.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Maximum")
public class Maximum extends ProfileStatistic {
  public Maximum() {

  }

  public Maximum(DataProfile d) {
    super(max, dti, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    Object val = null;
    if (oldVal == null) val = getBasicInstance();
    else val = oldVal;
    for (Record r : rs) {
      Object field = r.getField(a);
      val = getMaximum(val, field, false);
    }
    this.setValue(val);
    this.setNumericVal(((Number) val).doubleValue());
    this.setValueClass(a.getDataType());
  }

  /**
   * Creates a basic instance used as a reference (in this case the minimum value)
   * 
   * @return the reference value
   */
  private Object getBasicInstance() {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MIN_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MIN_VALUE);
    else return Integer.MIN_VALUE;
  }

  /**
   * Checks the maximum value of two objects
   * 
   * @param current       the current maximum value
   * @param toComp        the new value to compare
   * @param isNumericList check, if calculation is performed with numeric list
   * @return the new maximum value
   */
  private Object getMaximum(Object current, Object toComp, boolean isNumericList) {
    if (toComp == null) return current;
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.max((long) current, ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.max((double) current, ((Number) toComp).doubleValue());
    else if (a.getDataType().equals(String.class) && !isNumericList) return Integer.max((int) current, ((String) toComp).length());
    else return Integer.max((int) current, ((Number) toComp).intValue());
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
      Object val = null;
      if (oldVal == null) val = getMaximum(list.get(list.size() - 1), getBasicInstance(), true);
      else val = getMaximum(list.get(list.size() - 1), oldVal, true);
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
		
		rdpVal = rdpVal + (Math.abs(rdpVal) * threshold);	// shift by threshold
		boolean conf = dpValue <= rdpVal;		
		if(!conf && Constants.DEBUG) System.out.println(StatisticTitle.max + " exceeded: " + dpValue + " > " + rdpVal + " (originally: " + this.getNumericVal() + ")");
		return conf;
		
	}
}
