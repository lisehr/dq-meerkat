package util.validators;

import dsd.records.Record;

public class EqualsValidator extends Validator{
	
	private final String attributeA;
	private final String attributeB;
	
	
	
	public EqualsValidator(String attributeA, String attributeB) {
		super();
		this.attributeA = attributeA;
		this.attributeB = attributeB;
	}



	@Override
	public boolean validate(Record r) {
		Object a = r.getField(attributeA);
		Object b = r.getField(attributeB);
		if((a==null)!=(b==null)){
			return false;
		}
		if(a==null) return true;
		return a.equals(b);
	}

}
