package dqm.jku.dqmeerkat.util.validators;

import dqm.jku.dqmeerkat.dsd.records.Record;

public abstract class Validator {
	
	public abstract boolean validate(Record r);

}
