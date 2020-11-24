package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.dt;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.*;


/**
 * Describes the metric Data Type, which is a higher granularity than Basic
 * type, showing the Java class of the values.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/DataType")
public class DataType extends ProfileMetric {
  public DataType() {

  }

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

}
