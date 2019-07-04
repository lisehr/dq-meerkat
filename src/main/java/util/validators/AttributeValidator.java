package util.validators;

import dsd.elements.Attribute;
import dsd.records.Record;

public abstract class AttributeValidator extends Validator {

	protected final Attribute attribute;
	
	@Override
	public abstract boolean validate(Record r);
	
	public AttributeValidator(Attribute a) {
		this.attribute = a;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}

}
