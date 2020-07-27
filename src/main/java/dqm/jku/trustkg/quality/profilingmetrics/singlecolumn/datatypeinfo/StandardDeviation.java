package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.numrows;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.avg;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.sd;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.dti;

import java.util.List;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.NumRows;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

public class StandardDeviation extends DependentProfileMetric {
	public StandardDeviation() {

	}

	public StandardDeviation(DataProfile d) {
		super(sd, dti, d);
	}

	private void calculation(RecordList rl, Object oldVal, boolean checked) {
		if (!checked) this.dependencyCalculationWithRecordList(rl);
		Object avgVal = super.getRefProf().getMetric(avg).getValue();
		Object val = null;
		if (oldVal == null) val = getBasicInstance();
		else val = oldVal;
		for (Record r : rl) {
			Object field = r.getField((Attribute) super.getRefElem());
			val = addValue(val, field, avgVal);
		}
		val = performAveraging(val);
		this.setValue(val);

		Attribute a = (Attribute) super.getRefElem();
		this.setValueClass(a.getDataType());

	}

	/**
	 * Adds a value to the sum of squared differences
	 * 
	 * @param current the current sum of values
	 * @param toAdd   the value to be added
	 * @param avg     the average value to substract
	 * @return the new sum of values
	 */
	private Object addValue(Object current, Object toAdd, Object avg) {
		if (toAdd == null) return current;
		Attribute a = (Attribute) super.getRefElem();
		if (a.getDataType().equals(Long.class)) return (long) current + (((Number) toAdd).longValue() - ((Number) avg).longValue()) * (((Number) toAdd).longValue() - ((Number) avg).longValue());
		else if (a.getDataType().equals(Double.class)) return (double) current + (((Number) toAdd).doubleValue() - ((Number) avg).doubleValue()) * (((Number) toAdd).doubleValue() - ((Number) avg).doubleValue());
		else if (toAdd.getClass().equals(String.class)) return (int) current + (((String) toAdd).length() - ((String) avg).length()) * (((String) toAdd).length() - ((String) avg).length());
		else return (int) current + ((int) toAdd - (int) avg) * ((int) toAdd - (int) avg);
	}

	/**
	 * Method for getting the square root of the average value of the objects
	 * 
	 * @param sum the sum of values
	 * @return the square root of the average value 
	 */
	private Object performAveraging(Object sum) {
		Attribute a = (Attribute) super.getRefElem();
		if (a.getDataType().equals(Long.class)) return (((long) sum / ((int) super.getRefProf().getMetric(numrows).getValue() - 1)) ^ (1/2));
		else if (a.getDataType().equals(Double.class)) return Math.sqrt(((double) sum / ((int) super.getRefProf().getMetric(numrows).getValue() - 1)));
		return ((int) sum / ((int) super.getRefProf().getMetric(numrows).getValue() - 1)) ^ (1/2);
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
			Object avgVal = super.getRefProf().getMetric(avg).getValue();
			list.sort(new NumberComparator());
			Object val = null;
			if (oldVal == null) val = getBasicInstance();
			else val = oldVal;
			for (Number n : list) {
				val = addValue(val, n, avgVal);
			}
			val = performAveraging(val);
			this.setValue(val);

		}
		Attribute a = (Attribute) super.getRefElem();
		this.setValueClass(a.getDataType());
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
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(avg)) super.getRefProf().getMetric(avg).calculationNumeric(list, null);
	}

	@Override
	protected void dependencyCalculationWithRecordList(RecordList rl) {
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculation(rl, null);
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(avg)) super.getRefProf().getMetric(avg).calculation(rl, null);
	}

	@Override
	protected void dependencyCheck() {
		ProfileMetric numrowM = super.getRefProf().getMetric(numrows);
		if (numrowM == null) {
			numrowM = new NumRows(super.getRefProf());
			super.getRefProf().addMetric(numrowM);
		}
		ProfileMetric avgM = super.getRefProf().getMetric(avg);
		if (avgM == null) {
			avgM = new Average(super.getRefProf());
			super.getRefProf().addMetric(avgM);
		}

	}

	/**
	 * Creates a basic instance used as a reference (in this case zero as a number)
	 * 
	 * @return the reference value
	 */
	private Object getBasicInstance() {
		Attribute a = (Attribute) super.getRefElem();
		if (a.getDataType().equals(Long.class)) return Long.valueOf(0);
		else if (a.getDataType().equals(Double.class)) return Double.valueOf(0);
		else return Integer.valueOf(0);
	}

}
