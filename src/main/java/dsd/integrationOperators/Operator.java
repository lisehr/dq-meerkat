package dsd.integrationOperators;

import java.util.Set;

import dsd.elements.Concept;
import dsd.elements.Datasource;
import dsd.integrated.IntegratedConcept;
import dsd.records.Record;
import util.AttributeSet;

public abstract class Operator {

	protected final IntegratedConcept iconcept;

	public Operator(IntegratedConcept iconcept) {
		super();
		this.iconcept = iconcept;
	}

	public IntegratedConcept getIconcept() {
		return iconcept;
	}

	public abstract AttributeSet getAttributes();

	public abstract Iterable<Record> getRecords();

	public abstract int getNrRecords();

	public abstract Set<Concept> getDependendConcepts();

	public abstract Set<Datasource> getDependendDatasources();

}
