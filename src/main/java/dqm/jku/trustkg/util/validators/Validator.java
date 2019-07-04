package dqm.jku.trustkg.util.validators;

import dqm.jku.trustkg.dsd.records.Record;

public abstract class Validator {
	
	public abstract boolean validate(Record r);

}
