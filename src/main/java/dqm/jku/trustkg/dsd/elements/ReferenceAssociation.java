package dqm.jku.trustkg.dsd.elements;

public class ReferenceAssociation extends Association {

	private static final long serialVersionUID = 1L;

	// Neo4J Attribute Fields
	private String neo4jtype;
	private int neo4jcount;

	public ReferenceAssociation(String label, Datasource datasource) {
		super(label, datasource);
	}

	public void setNeo4JType(String type) {
		this.neo4jtype = type;
	}

	public String getNeo4JType() {
		return neo4jtype;
	}

	public void setNeo4JCount(int count) {
		this.neo4jcount = count;
	}

	public int getNeo4JCount() {
		return neo4jcount;
	}

}
