package dqm.jku.dqmeerkat.dsd.elements;

public abstract class Constraint extends DSDElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Datasource datasource;

	Constraint(String label, Datasource datasource) {
		super(label);
		this.datasource = datasource;
	}

	@Override
	public String getURI() {
		return datasource.getURI() + "/" + label;
	}

	public Datasource getDatasource() {
		return datasource;
	}

}
