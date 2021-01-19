package dqm.jku.dqmeerkat.dsd.elements;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class Association extends Concept {

	private static final long serialVersionUID = 1L;

	public Association(String label, Datasource datasource) {
		super(label, datasource);
	}

	/**
	 * @return all Concepts associated with this Association, excluding this
	 */
	public Set<Concept> getConcepts() {
		return foreignKeys.stream().map(x -> x.getReferencedConcept()).collect(Collectors.toSet());
	}

	@Override
	public void addForeignKey(ForeignKey foreignKey) {
		if (foreignKey.getReferencingConcept() == this) {
			super.addForeignKey(foreignKey);
		} else {
			throw new IllegalArgumentException("Foreignkey is not connected to this Concept.");
		}
	}

}
