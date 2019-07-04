package dqm.jku.trustkg.util.validators;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;

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
