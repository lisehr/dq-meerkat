package dqm.jku.dqmeerkat.util.validators;

import dqm.jku.dqmeerkat.dsd.records.Record;

public class NotValidator extends Validator {
	
	private final Validator left;
	

	public NotValidator(Validator left) {
		super();
		this.left = left;
	}


	@Override
	public boolean validate(Record r) {
		return !left.validate(r);
	}

}
