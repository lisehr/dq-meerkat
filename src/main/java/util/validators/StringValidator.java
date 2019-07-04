package util.validators;

import dsd.elements.Attribute;
import dsd.records.Record;

public abstract class StringValidator extends AttributeValidator {

	public StringValidator(Attribute a) {
		super(a);
	}

	@Override
	public abstract boolean validate(Record r);
	
	public String getValue(Record r) {
		Object o = r.getField(attribute);
		
		if(!attribute.getDataType().isInstance(o)) {
			return null;
		}
		
		if(o instanceof String) {
			return o.toString();
		}
		
		return null;
	}

}
