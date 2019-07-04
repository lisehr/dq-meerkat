package dsd.integrated;

import dsd.DSDFactory;
import dsd.elements.Attribute;
import dsd.elements.Concept;
import dsd.integrationOperators.Operator;
import dsd.records.Record;

public class IntegratedConcept extends Concept {

	private static final long serialVersionUID = 1L;
	private Operator integrationOperator;

	public Operator getIntegrationOperator() {
		return integrationOperator;
	}

	public IntegratedConcept(String label, IntegratedDatasource datasource) {
		super(label, datasource);
	}

	public Iterable<Record> getRecords() {
		return integrationOperator.getRecords();
	}

	public void initIntegrationOperator(Operator integrationOperator) {
		if (this.integrationOperator != null || integrationOperator == null)
			throw new UnsupportedOperationException("Integration Operator can only be set once and must not be null");
		this.integrationOperator = integrationOperator;
		for (Attribute a : integrationOperator.getAttributes()) {
			DSDFactory.makeAttribute(this, a);
		}
		((IntegratedDatasource) this.getDatasource()).integratedDatasources
				.addAll(integrationOperator.getDependendDatasources());
	}

}
