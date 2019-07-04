package util.validators;

import dsd.records.Record;

public class OrValidator extends Validator {
	
	private final Validator left;
	private final Validator right;
	

	public OrValidator(Validator left, Validator right) {
		super();
		this.left = left;
		this.right = right;
	}


	@Override
	public boolean validate(Record r) {
		return left.validate(r)||right.validate(r);
	}

}
