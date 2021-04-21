package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;


/**
 * Describes the metric Minimum, which is the minimum value of all values of an
 * Attribute.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Minimum")
public class Minimum extends ProfileStatistic {
  public Minimum() {

  }

  public Minimum(DataProfile d) {
    super(min, dti, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    Object val = null;
    if (oldVal == null) val = getBasicInstance();
    else val = oldVal;
    for (Record r : rs) {
      Object field = r.getField(a);
      val = getMinimum(val, field, false);
    }
    this.setValue(val);
    this.setNumericVal(((Number) val).doubleValue());
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
   * @param current       the current minimum value
   * @param toComp        the new value to compare
   * @param isNumericList check, if calculation happens with numeric list
   * @return the new minimum value
   */
  private Object getMinimum(Object current, Object toComp, boolean isNumericList) {
    if (toComp == null) return current;
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.min(((Number) current).longValue(), ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.min(((Number) current).doubleValue(), ((Number) toComp).doubleValue());
    else if (a.getDataType().equals(String.class) && !isNumericList) return Integer.min((int) current, ((String) toComp).length());
    else return Integer.min(((Number) current).intValue(), ((Number) toComp).intValue());
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
      if (oldVal == null) val = getMinimum(list.get(0), getBasicInstance(), true);
      else val = getMinimum(list.get(0), oldVal, true);
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
		
		rdpVal = rdpVal - (Math.abs(rdpVal) * threshold);	// shift by threshold
		boolean conf = dpValue >= rdpVal;
		if(!conf && Constants.DEBUG) System.out.println(StatisticTitle.min + " exceeded: " + dpValue + " < " + rdpVal + " (originally: " + this.getNumericVal() + ")");
		return conf;
	}
}
