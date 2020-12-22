package dqm.jku.dqmeerkat.quality.profilingmetrics.singlecolumn.datatypeinfo;

import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricTitle.*;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingmetrics.ProfileMetric;
import dqm.jku.dqmeerkat.util.Constants;


/**
 * Describes the metric Basic Type, which categorizes an Attribute as a String,
 * Null or Numeric value type.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/BasicType")
public class BasicType extends ProfileMetric {
  public BasicType() {

  }

  public BasicType(DataProfile d) {
    super(bt, dti, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    if (((Attribute) super.getRefElem()).getDataType().equals(String.class)) super.setValue("String");
    else if (((Attribute) super.getRefElem()).getDataType().equals(Object.class)) super.setValue("Null");
    else super.setValue("Numeric");
    this.setNumericVal(this.getValue());
    super.setValueClass(String.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
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
public boolean checkConformance(ProfileMetric m, double threshold) {
	String rdpVal = this.getSimpleValueString();
	String dpValue = this.getSimpleValueString();
	
	boolean conf = rdpVal.equals(dpValue);
	if(!conf && Constants.DEBUG) System.out.println(this.getTitle() + " exceeded: " + dpValue + " != " + rdpVal);
	return conf;
}
}
