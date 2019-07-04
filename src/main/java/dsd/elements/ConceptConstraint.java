package dsd.elements;

import java.util.HashSet;

import dsd.records.Record;
import quality.DataQualityStore;
import util.Constants;
import util.validators.Validator;

public class ConceptConstraint extends Constraint {

	private static final long serialVersionUID = 1L;

	private Validator validator;
	private final Concept concept;
	private HashSet<Attribute> attributes = new HashSet<Attribute>();

	public ConceptConstraint(String label, Datasource datasource, Concept concept, Validator validator) {
		super(label, datasource);
		this.concept = concept;
		this.validator = validator;
	}

	public HashSet<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	public Concept getConcept() {
		return concept;
	}

	public boolean validate(Record r) {
		boolean ret = validator.validate(r);
		if (Constants.STORE_CONSTRAINT_VIOLATIONS && !ret) {
			DataQualityStore.getDQRecords().addViolationRecord(this, r);
		}
		return ret;
	}

	public Validator getValidator() {
		return validator;
	}

}
