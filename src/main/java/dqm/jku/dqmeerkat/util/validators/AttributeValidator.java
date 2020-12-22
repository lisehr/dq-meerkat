package dqm.jku.dqmeerkat.util.validators;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;

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
