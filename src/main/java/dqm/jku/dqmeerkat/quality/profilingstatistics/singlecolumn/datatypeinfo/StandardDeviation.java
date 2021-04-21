package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.avg;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numrows;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.sd;

import java.math.BigInteger;
import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;

@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/StandardDeviation")
public class StandardDeviation extends DependentProfileStatistic {
	public StandardDeviation() {

	}

	public StandardDeviation(DataProfile d) {
		super(sd, dti, d);
	}

	private void calculation(RecordList rl, Object oldVal, boolean checked) {
		if (!checked) this.dependencyCalculationWithRecordList(rl);
		Object avgVal = super.getRefProf().getStatistic(avg).getValue();
		Object val = null;
		if (oldVal == null) val = getBasicInstance();
		else val = oldVal;
		for (Record r : rl) {
			Object field = r.getField((Attribute) super.getRefElem());
			val = addValue(val, field, avgVal);
		}
		val = performAveraging(val);
		this.setValue(val);
		this.setNumericVal(((Number) val).doubleValue());
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
	private Object addValue(Object current, Object toAdd, Object avg) {
		if (toAdd == null) return current;
		Attribute a = (Attribute) super.getRefElem();
		if (a.getDataType().equals(Long.class)) return (long) current + powerN(((((Number) toAdd).longValue() - ((Number) avg).longValue())), 2);
		else if (a.getDataType().equals(Double.class)) return (double) current + Math.pow((((Number) toAdd).doubleValue() - ((Number) avg).doubleValue()), 2);
		else if (a.getClass().equals(String.class)) return (int) current + (int) Math.pow((double)(((String) toAdd).length() - ((String) avg).length()), 2);
		else return (numberToBigInteger(current).add(numberToBigInteger(toAdd).subtract(numberToBigInteger(avg)).pow(2)));
	}
	
	private BigInteger numberToBigInteger(Object i) {
		if (i.getClass().equals(BigInteger.class)) return (BigInteger) i;
		else if (i.getClass().equals(Long.class)) return BigInteger.valueOf((long) i);
		return BigInteger.valueOf((int) i);
	}

	private long powerN(long number, int power) {
		long res = 1;
		long sq = number;
		while (power > 0) {
			if (power % 2 == 1) {
				res *= sq;
			}
			sq = sq * sq;
			power /= 2;
		}
		return res;
	}

	/**
	 * Method for getting the square root of the average value of the objects
	 * 
	 * @param sum the sum of values
	 * @return the square root of the average value
	 */
	private Object performAveraging(Object sum) {
		if (((long) super.getRefProf().getStatistic(numrows).getValue()) == 1) return sum;
		Attribute a = (Attribute) super.getRefElem();
		if (a.getDataType().equals(Long.class)) return Math.sqrt((((long) sum / ((long) super.getRefProf().getStatistic(numrows).getValue() - 1))));
		else if (a.getDataType().equals(Double.class)) return Math.sqrt(((double) sum / ((long) super.getRefProf().getStatistic(numrows).getValue() - 1)));
		return Math.sqrt((numberToBigInteger(sum).divide((numberToBigInteger(super.getRefProf().getStatistic(numrows).getValue())).subtract(BigInteger.ONE)).doubleValue()));
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
			Object avgVal = super.getRefProf().getStatistic(avg).getValue();
			list.sort(new NumberComparator());
			Object val = null;
			if (oldVal == null) val = getBasicInstance();
			else val = oldVal;
			for (Number n : list) {
				val = addValue(val, n, avgVal);
			}
			val = performAveraging(val);
			this.setValue(val);
			this.setNumericVal(((Number) val).doubleValue());
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
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows)) super.getRefProf().getStatistic(numrows).calculationNumeric(list, null);
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(avg)) super.getRefProf().getStatistic(avg).calculationNumeric(list, null);
	}

	@Override
	protected void dependencyCalculationWithRecordList(RecordList rl) {
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows)) super.getRefProf().getStatistic(numrows).calculation(rl, null);
		if (super.getMetricPos(sd) - 1 <= super.getMetricPos(avg)) super.getRefProf().getStatistic(avg).calculation(rl, null);
	}

	@Override
	protected void dependencyCheck() {
		ProfileStatistic numrowM = super.getRefProf().getStatistic(numrows);
		if (numrowM == null) {
			numrowM = new NumRows(super.getRefProf());
			super.getRefProf().addStatistic(numrowM);
		}
		ProfileStatistic avgM = super.getRefProf().getStatistic(avg);
		if (avgM == null) {
			avgM = new Average(super.getRefProf());
			super.getRefProf().addStatistic(avgM);
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
