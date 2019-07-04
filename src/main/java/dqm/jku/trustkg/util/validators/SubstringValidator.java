package dqm.jku.trustkg.util.validators;

import dqm.jku.trustkg.dsd.records.Record;

public class SubstringValidator extends Validator{
	String left;
	String right; 
	boolean leftc;
	boolean rightc;

	

	public SubstringValidator(boolean leftc, boolean rightc,String left, String right) {
		super();
		this.left = left;
		this.right = right;
		this.leftc = leftc;
		this.rightc = rightc;
	}

	@Override
	public boolean validate(Record r){
		Object a = leftc?left:r.getField(left);
		Object b = rightc?right:r.getField(right);
		if((a==null)&&(b==null)){
			return false;
		}
		if((a==null)||(b==null)){
			return false;
		}
		
		if(a instanceof String && b instanceof String){
			String sb = (String) b;
			return sb.toLowerCase().contains(((String) a).toLowerCase());
		}
		throw new IllegalArgumentException("These values can not be compared (yet): "+a.getClass()+" and "+ b.getClass());
	
	}

}
