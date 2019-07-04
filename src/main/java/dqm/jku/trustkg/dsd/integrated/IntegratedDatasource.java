package dqm.jku.trustkg.dsd.integrated;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import dqm.jku.trustkg.dsd.elements.Datasource;

public class IntegratedDatasource extends Datasource {

	private static final long serialVersionUID = -4890554022893965516L;
	final Set<Datasource> integratedDatasources = new HashSet<Datasource>();

	public IntegratedDatasource(String label) {
		super(label);
	}

	public Set<Datasource> getIntegratedDatasource() {
		return Collections.unmodifiableSet(integratedDatasources);
	}

}
