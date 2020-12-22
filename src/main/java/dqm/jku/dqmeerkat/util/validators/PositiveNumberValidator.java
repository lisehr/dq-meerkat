package dqm.jku.dqmeerkat.util.validators;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;

public class PositiveNumberValidator extends NumberValidator {
	
	private boolean zeroAllowed;

	public PositiveNumberValidator(Attribute a, boolean zeroAllowed) {
		super(a);
		this.zeroAllowed = zeroAllowed;
	}
	
	@Override
	public boolean validate(Record r) {
		Double d = getValue(r);
		
		return d != null && (d > 0 || (zeroAllowed && d == 0));
	}

}
