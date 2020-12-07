package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.numrows;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.med;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.mad;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.sd;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.dti;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.NumRows;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/MedianAbsoluteDeviation")
public class MedianAbsoluteDeviation extends DependentProfileMetric {
	public MedianAbsoluteDeviation() {

	}

	public MedianAbsoluteDeviation(DataProfile d) {
		super(mad, dti, d);
	}

	private void calculation(RecordList rl, Object oldVal, boolean checked) {
		if (!checked) this.dependencyCalculationWithRecordList(rl);
		Object medVal = super.getRefProf().getMetric(med).getValue();
		List<Number> medians = new ArrayList<>();
		for (Record r : rl) {
			Object field = r.getField((Attribute) super.getRefElem());
			medians.add((Number) subValue(field, medVal));
		}
		Median medM = new Median(this.getRefProf());
		medM.calculationNumeric(medians, null);
		Object med = medM.getValue();
		this.setValue(med);
		this.setNumericVal(((Number) med).doubleValue());
		this.setValueClass(Double.class);
	}

	/**
	 * Adds a value to the sum of squared differences
	 * 
	 * @param current the current sum of values
	 * @param toAdd   the value to be added
	 * @param avg     the average value to substract
	 * @return the new sum of values
	 */
	private Object subValue(Object toAdd, Object med) {
		if (toAdd == null) return 0;
		Attribute a = (Attribute) super.getRefElem();
		if (a.getDataType().equals(Long.class)) return (long) toAdd - (long) med;
		else if (a.getDataType().equals(Double.class)) return (double) toAdd - (double) med;
		else if (a.getClass().equals(String.class)) return (int) toAdd - (int) med;
		else return (intToBigInteger(toAdd).subtract(intToBigInteger(med)));
	}
	
	private BigInteger intToBigInteger(Object i) {
		if (i.getClass().equals(BigInteger.class)) return (BigInteger) i;
		return BigInteger.valueOf((int) i);
	}

	@Override
	public void calculation(RecordList rl, Object oldVal) {
		calculation(rl, oldVal, false);
	}

	@Override
	public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
		this.dependencyCalculationWithNumericList(list);
		if (list == null || list.isEmpty()) {
			if (oldVal != null) return;
			else this.setValue(null);
		} else {
			Object medVal = super.getRefProf().getMetric(med).getValue();
			list.sort(new NumberComparator());
			List<Number> medians = new ArrayList<>();
			for (Number n : list) {
				if (new BigDecimal(n.toString()).compareTo(new BigDecimal(medVal.toString())) == -1) medians.add((Number) subValue(medVal, n));
				else medians.add((Number) subValue(n, medVal));
			}
			Median medM = new Median(this.getRefProf());
			medM.calculationNumeric(medians, null);
			Object med = medM.getValue();
			this.setValue(med);
			this.setNumericVal(((Number) med).doubleValue());
		}
		this.setValueClass(Double.class);
	}

	@Override
	public void update(RecordList rl) {
		calculation(rl, null, true);
	}

	@Override
	protected String getValueString() {
		return super.getSimpleValueString();
	}

	@Override
	protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculationNumeric(list, null);
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(med)) super.getRefProf().getMetric(med).calculationNumeric(list, null);
	}

	@Override
	protected void dependencyCalculationWithRecordList(RecordList rl) {
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculation(rl, null);
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(med)) super.getRefProf().getMetric(med).calculation(rl, null);
	}

	@Override
	protected void dependencyCheck() {
		ProfileMetric numrowM = super.getRefProf().getMetric(numrows);
		if (numrowM == null) {
			numrowM = new NumRows(super.getRefProf());
			super.getRefProf().addMetric(numrowM);
		}
		ProfileMetric medM = super.getRefProf().getMetric(med);
		if (medM == null) {
			medM = new Median(super.getRefProf());
			super.getRefProf().addMetric(medM);
		}

	}

	@Override
	public boolean checkConformance(ProfileMetric m, double threshold) {
		Number rdpVal = (Number) this.getNumericVal();
		Number dpValue = (Number) m.getValue();
		return ((Math.abs(rdpVal.doubleValue() - dpValue.doubleValue()) < threshold));
	}
}
