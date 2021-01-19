package dqm.jku.dqmeerkat.util.validators;

import dqm.jku.dqmeerkat.dsd.records.Record;

public class ComparisonValidator extends Validator{
	
	private final String attributeA;
	private final String attributeB;
	private final Comaparison comp;
	
	public enum Comaparison{
		SMALLER, SMALLER_EQUALS, GREATER, GREATER_EQUALS;
	}
	
	
	public ComparisonValidator(String attributeA, String attributeB, Comaparison c ) {
		super();
		this.attributeA = attributeA;
		this.attributeB = attributeB;
		comp = c;
	}
	
	private boolean validate(int c){
		switch(comp){
		case SMALLER: return c<0;
		case SMALLER_EQUALS: return c<=0;
		case GREATER: return c>0;
		case GREATER_EQUALS: return c>=0;
		default: throw new IllegalArgumentException("This method of comparison does not exist");
		}
		
	}



	@Override
	public boolean validate(Record r) {
		Object a = r.getField(attributeA);
		Object b = r.getField(attributeB);
		if((a==null)||(b==null)){
			return false;
		}
		
		if(!a.getClass().equals(b.getClass())){return false;}
		if(!a.getClass().equals(Long.class)){
			return validate(((Long)a).compareTo((Long) b));
		}
		if(!a.getClass().equals(Double.class)){
			return validate(((Double)a).compareTo((Double) b));
		}
		throw new IllegalArgumentException("These values can not be compared (yet): "+a.getClass()+" and "+ b.getClass());
	}

}
