package util.validators;

import dsd.records.Record;

public class ConstEqualsValidator extends Validator{
	
	private final String const_;
	private final String attributeB;
	
	
	
	public ConstEqualsValidator(String const_, String attributeB) {
		super();
		this.const_ = const_;
		this.attributeB = attributeB;
	}



	@Override
	public boolean validate(Record r) {
		Object a = const_;
		Object b = r.getField(attributeB);
		if((a==null)!=(b==null)){
			return false;
		}
		if(a==null) return true;
		return a.equals(b);
	}

}
