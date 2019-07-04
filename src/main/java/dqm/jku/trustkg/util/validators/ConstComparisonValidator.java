package dqm.jku.trustkg.util.validators;

import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.util.validators.ComparisonValidator.Comaparison;

public class ConstComparisonValidator extends Validator {

	private final String constant;
	private final String attributeB;
	private final Comaparison comp;

	public ConstComparisonValidator(String constant, String attributeB, Comaparison c) {
		super();
		this.constant = constant;
		this.attributeB = attributeB;
		comp = c;
	}

	private boolean validate(int c) {
		switch (comp) {
		case SMALLER:
			return c < 0;
		case SMALLER_EQUALS:
			return c <= 0;
		case GREATER:
			return c > 0;
		case GREATER_EQUALS:
			return c >= 0;
		default:
			throw new IllegalArgumentException("This method of comparison does not exist");
		}

	}

	@Override
	public boolean validate(Record r) {
		Object a = constant;
		Object b = r.getField(attributeB);
		if ((a == null) && (b == null)) {
			return true;
		}

		if ((a == null) || (b == null)) {
			return false;
		}

		if (!a.getClass().equals(b.getClass())) {
			return false;
		}
		if (!a.getClass().equals(Long.class)) {
			return validate(((Long) a).compareTo((Long) b));
		}
		if (!a.getClass().equals(Double.class)) {
			return validate(((Double) a).compareTo((Double) b));
		}
		throw new IllegalArgumentException("These values can not be compared (yet): " + a.getClass() + " and " + b.getClass());
	}

}
