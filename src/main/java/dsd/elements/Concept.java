package dsd.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import util.AttributeSet;

public class Concept extends DSDElement {

	private static final long serialVersionUID = 1L;
	private final Datasource datasource;
	private HashSet<Attribute> attributes = new HashSet<Attribute>();
	private HashSet<Attribute> primaryKey = new HashSet<Attribute>();
	private List<FunctionalDependency> functionalDependencies = new ArrayList<FunctionalDependency>();
	protected Set<ForeignKey> foreignKeys = new HashSet<ForeignKey>();

	public Concept(String label, Datasource datasource) {
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

	public AttributeSet getAttributes() {
		return new AttributeSet(attributes);
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	public boolean containsAttribute(Attribute attribute) {
		return attributes.contains(attribute) && attribute.getConcept() == this;
	}

	public AttributeSet getPrimaryKey() {
		return new AttributeSet(primaryKey);
	}

	public void addPrimaryKeyAttribute(Attribute primaryKey) {
		if (attributes.contains(primaryKey)) {
			this.primaryKey.add(primaryKey);
		} else {
			throw new IllegalArgumentException("Primary key is not contained in attributes list of concept.");
		}
	}

	public void addFunctionalDependency(FunctionalDependency fd) {
		functionalDependencies.add(fd);
		Collections.sort(functionalDependencies);
	}

	public List<Attribute> getSortedAttributes() {
		List<Attribute> list = new ArrayList<Attribute>(attributes);
		Collections.sort(list, new Comparator<Attribute>() {

			@Override
			public int compare(Attribute o1, Attribute o2) {
				return Integer.compare(o1.getOrdinalPosition(), o2.getOrdinalPosition());
			}

		});
		return list;
	}

	public List<FunctionalDependency> getFunctionalDependencies() {
		return Collections.unmodifiableList(functionalDependencies);
	}

	public Attribute getAttribute(String name) {
		for (Attribute a : attributes) {
			if (a.getLabel().equalsIgnoreCase(name))
				return a;
		}
		return null;
	}

	public List<FunctionalDependency> getFunctionalDependencies(Attribute right) {
		List<FunctionalDependency> res = new ArrayList<FunctionalDependency>();
		for (FunctionalDependency fd : functionalDependencies) {
			if (fd.getRightSide().equals(right))
				res.add(fd);
		}
		return res;
	}

	public List<FunctionalDependency> getFunctionalDependencies(AttributeSet left) {
		List<FunctionalDependency> res = new ArrayList<FunctionalDependency>();
		for (FunctionalDependency fd : functionalDependencies) {
			if (fd.getLeftSide().equals(left))
				res.add(fd);
		}
		return res;
	}

	public List<FunctionalDependency> getViableFunctionalDependencies(AttributeSet left) {
		List<FunctionalDependency> res = new ArrayList<FunctionalDependency>();
		for (FunctionalDependency fd : functionalDependencies) {
			if (left.contains(fd.getLeftSide()))
				res.add(fd);
		}
		return res;
	}

	public Set<ForeignKey> getForeignKeys() {
		return Collections.unmodifiableSet(foreignKeys);
	}

	public Set<ForeignKey> getReferencingForeignKeys() {
		return foreignKeys.stream().filter(x -> x.getReferencingConcept() == this).collect(Collectors.toSet());
	}

	public Set<ForeignKey> getReferencedForeignKeys() {
		return foreignKeys.stream().filter(x -> x.getReferencedConcept() == this).collect(Collectors.toSet());
	}

	public void addForeignKey(ForeignKey foreignKey) {
		if (foreignKey.getReferencedConcept() == this || foreignKey.getReferencingConcept() == this) {
			foreignKeys.add(foreignKey);
		} else {
			throw new IllegalArgumentException("Foreignkey is not connected to this Concept.");
		}
	}

}
