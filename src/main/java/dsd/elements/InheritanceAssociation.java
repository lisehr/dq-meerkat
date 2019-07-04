package dsd.elements;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InheritanceAssociation extends Association {

	private static final long serialVersionUID = 1L;

	private ForeignKey parent;
	private boolean isDisjoint;
	private boolean isComplete;

	public InheritanceAssociation(String label, Datasource datasource) {
		super(label, datasource);
	}

	public Concept getParent() {
		return parent.getReferencedConcept();
	}

	public void setParent(ForeignKey parent) {
		this.parent = parent;
	}

	public void setParent(Concept parent) {
		List<ForeignKey> fkList = this.foreignKeys.stream().filter(x -> x.getReferencedConcept() == parent).collect(Collectors.toList());
		if (fkList.size() != 1) {
			throw new IllegalArgumentException("Can not infer aggregate direction from Concept. Please add ForeignKey directly.");
		}
		this.parent = fkList.get(0);
	}

	public Set<Concept> getChildren() {
		return foreignKeys.stream().map(x -> x.getReferencedConcept()).filter(x -> x.equals(getParent())).collect(Collectors.toSet());
	}

	public boolean isDisjoint() {
		return isDisjoint;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setDisjoint(boolean isDisjoint) {
		this.isDisjoint = isDisjoint;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	@Override
	public void addForeignKey(ForeignKey foreignKey) {
		if (!getConcepts().contains(foreignKey.getReferencedConcept())) {
			super.addForeignKey(foreignKey);
		} else {
			throw new IllegalArgumentException("Inheritance Association does not allow recursive foreign key connections.");
		}
	}

}
