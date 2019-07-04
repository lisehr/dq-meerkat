package dqm.jku.trustkg.dsd.integrationOperators;

import java.util.Set;

import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.integrated.IntegratedConcept;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.util.AttributeSet;

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
