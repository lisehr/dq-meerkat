package dqm.jku.trustkg.util.validators;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;

public class NumberRangeValidator extends NumberValidator {

	private final double min;
	private final double max;

	/**
	 * @param a
	 *            Attribute which is checked
	 * @param min
	 *            inclusive
	 * @param max
	 *            exclusive
	 */
	public NumberRangeValidator(Attribute a, double min, double max) {
		super(a);
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean validate(Record r) {
		Double d = getValue(r);
		return d != null && d < max && d >= min;
	}

}
