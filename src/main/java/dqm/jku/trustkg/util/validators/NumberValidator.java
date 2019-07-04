package dqm.jku.trustkg.util.validators;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;

public class NumberValidator extends AttributeValidator {

	public NumberValidator(Attribute a) {
		super(a);
	}

	@Override
	public boolean validate(Record r) {
		return getValue(r) != null;
	}
	
	public Double getValue(Record r) {
		Object o = r.getField(attribute);
		
		if(!attribute.getDataType().isInstance(o)) {
			return null;
		}
		
		if(Number.class.isAssignableFrom(attribute.getDataType())) {
			return ((Number) o).doubleValue();
		}
		
		if(o instanceof String) {
			double d = 0.0;
			try {
				d = Double.parseDouble((String) o);
			} catch(NumberFormatException e) {
				return null;
			}
			
			return d;
		}
		
		return null;
	}

}
