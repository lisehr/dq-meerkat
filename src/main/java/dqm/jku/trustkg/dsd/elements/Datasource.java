package dqm.jku.trustkg.dsd.elements;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public class Datasource extends DSDElement {

	private static final long serialVersionUID = 1L;
	private static final String prefix = "http://example.com/";

	private Set<Concept> concepts = new TreeSet<Concept>();
	private Set<Association> associations = new TreeSet<Association>();
	private Set<Constraint> constraints = new TreeSet<Constraint>();

	public Datasource(String label) {
		super(label);
	}

	@Override
	public String getURI() {
		return prefix + label;
	}

	public Set<Concept> getConcepts() {
		return Collections.unmodifiableSet(concepts);
	}

	public Set<Concept> getConceptsAndAssociations() {
		Set<Concept> unionSet = new TreeSet<Concept>();
		unionSet.addAll(concepts);
		unionSet.addAll(associations);
		return unionSet;
	}

	public void addConcept(Concept concept) {
		concepts.add(concept);
	}

	public Set<Association> getAssociations() {
		return Collections.unmodifiableSet(associations);
	}

	public Set<Association> getAssociations(Concept c) {
		Set<Association> assoc = new HashSet<Association>();
		for (Association ac : associations) {
			if (ac.getConcepts().contains(c))
				assoc.add(ac);
		}
		return assoc;
	}

	public <T extends Association> Set<T> getAssociations(Predicate<T> predicate, Class<T> clazz) {
		Set<T> assoc = new HashSet<T>();

		for (Association ac : associations) {
			if (clazz.isAssignableFrom(ac.getClass())) {
				@SuppressWarnings("unchecked")
				T ac1 = (T) ac;
				if (predicate.test(ac1)) {
					assoc.add(ac1);
				}
			}

		}
		return assoc;
	}

	public void addAssociation(Association association) {
		associations.add(association);
	}

	public Set<Constraint> getConstraints() {
		return Collections.unmodifiableSet(constraints);
	}

	public void addConstraint(Constraint constraint) {
		this.constraints.add(constraint);
	}

	public Set<ConceptConstraint> getConceptConstraints() {
		Set<ConceptConstraint> cc = new HashSet<ConceptConstraint>();

		for (Constraint con : constraints) {
			if (con instanceof ConceptConstraint)
				cc.add((ConceptConstraint) con);
		}

		return cc;
	}

	public Concept getConcept(String name) {
		for (Concept c : concepts) {
			if (c.getLabel().equalsIgnoreCase(name))
				return c;
		}
		return null;
	}

	public Association getAssociation(String name) {
		for (Association ac : associations) {
			if (ac.getLabel().equalsIgnoreCase(name))
				return ac;
		}
		return null;
	}

	public int getNumberOfConceptsAndAssociations() {
		return concepts.size() + associations.size();
	}

}
