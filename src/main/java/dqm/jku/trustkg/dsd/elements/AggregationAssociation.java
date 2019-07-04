package dqm.jku.trustkg.dsd.elements;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregationAssociation extends Association {

	private static final long serialVersionUID = 1L;

	private ForeignKey aggregate;

	public AggregationAssociation(String label, Datasource datasource) {
		super(label, datasource);
	}

	public Concept getAggregate() {
		return aggregate.getReferencedConcept();
	}

	public void setAggregate(ForeignKey aggregate) {
		this.aggregate = aggregate;
	}

	public void setAggregate(Concept aggregate) {
		List<ForeignKey> fkList = this.foreignKeys.stream().filter(x -> x.getReferencedConcept() == aggregate).collect(Collectors.toList());
		if (fkList.size() != 1) {
			throw new IllegalArgumentException("Can not infer aggregate direction from Concept. Please add ForeignKey directly.");
		}
		this.aggregate = fkList.get(0);
	}

	public Set<Concept> getParts() {
		return foreignKeys.stream().map(x -> x.getReferencedConcept()).filter(x -> x.equals(getAggregate())).collect(Collectors.toSet());
	}
}
