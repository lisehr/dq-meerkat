package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.dt;

import java.util.List;

import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;


/**
 * Describes the metric Data Type, which is a higher granularity than Basic
 * type, showing the Java class of the values.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/DataType")
public class DataType extends AbstractProfileStatistic {

  public DataType(DataProfile d) {
    super(dt, dti, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    super.setValue(((Attribute) super.getRefElem()).getDataType().getSimpleName());
    super.setNumericVal(((Attribute) super.getRefElem()).getDataType().getSimpleName());
    super.setValueClass(String.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    calculation(null, null);
  }

  @Override
  public void update(RecordList rs) {
    calculation(null, null);
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

  @Override
  public boolean checkConformance(ProfileStatistic<Object> m, double threshold) {	String rdpVal = this.getSimpleValueString();
	String dpValue = this.getSimpleValueString();
	
	boolean conf = rdpVal.equals(dpValue);
	if(!conf && Constants.DEBUG) System.out.println(this.getTitle() + " exceeded: " + dpValue + " != " + rdpVal);
	return conf;
 }
}
