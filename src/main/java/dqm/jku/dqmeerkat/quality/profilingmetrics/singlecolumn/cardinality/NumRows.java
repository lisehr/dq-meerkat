package dqm.jku.dqmeerkat.quality.profilingmetrics.singlecolumn.cardinality;

import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricTitle.*;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingmetrics.ProfileMetric;


/**
 * Describes the metric NumRows, which is the amount of rows in a data set.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/cardinality/Cardinality")
public class NumRows extends ProfileMetric {
  public NumRows() {

  }

  public NumRows(DataProfile d) {
    super(numrows, cardCat, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    super.setValue(rs.size());
    super.setNumericVal(((Number) rs.size()).longValue());
    super.setValueClass(Integer.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    super.setValue(list.size());
    super.setNumericVal(((Number) list.size()).longValue());
    super.setValueClass(Integer.class);
  }

  @Override
  public void update(RecordList rs) {
    int oldSize = (int) super.getValue();
    super.setValue(oldSize + rs.size());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

	@Override
	public boolean checkConformance(ProfileMetric m, double threshold) {
		// Never evaluates to false, because the reference is here the size of the RDP and should not be compared to the batch size of the DPs
		return true;
	}
}
