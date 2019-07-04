package dqm.jku.trustkg.util.validators;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;

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
