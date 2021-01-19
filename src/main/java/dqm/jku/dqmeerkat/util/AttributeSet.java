package dqm.jku.dqmeerkat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;

/**
 * @author Bernhard
 *
 */
public class AttributeSet implements Iterable<Attribute> {

	private List<Attribute> attributes = new ArrayList<Attribute>();

	public AttributeSet(Iterable<Attribute> left) {
		for (Attribute a : left) {
			if (a == null)
				throw new IllegalArgumentException("Attributes must not be null");
			attributes.add(a);
		}
		Collections.sort(attributes, new Comparator<Attribute>() {

			@Override
			public int compare(Attribute o1, Attribute o2) {
				return Integer.compare(o1.getOrdinalPosition(), o2.getOrdinalPosition());
			}

		});
	}

	public AttributeSet(Attribute attribute) {
		if (attribute == null)
			throw new IllegalArgumentException("Attributes must not be null");
		attributes.add(attribute);
	}

	public AttributeSet(Attribute... values) {
		for (Attribute a : values) {
			if (a == null)
				throw new IllegalArgumentException("Attributes must not be null");
			attributes.add(a);
		}

		Collections.sort(attributes, new Comparator<Attribute>() {

			@Override
			public int compare(Attribute o1, Attribute o2) {
				return Integer.compare(o1.getOrdinalPosition(), o2.getOrdinalPosition());
			}

		});
	}

	/**
	 * Empty Set constructor
	 */
	public AttributeSet() {

	}

	@Override
	public int hashCode() {
		int sum = 0;
		for (Attribute a : attributes) {
			sum += a.hashCode();
		}
		return sum;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeSet other = (AttributeSet) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		}
		if (attributes.size() != other.attributes.size())
			return false;
		for (int i = 0; i < attributes.size(); i++) {
			if (!attributes.get(i).equals(other.attributes.get(i)))
				return false;
		}
		return true;
	}

	public AttributeValueSet getAttributeValueSet(Record r) {
		AttributeValueSet values = new AttributeValueSet();
		for (Attribute a : attributes) {
			values.addValue(r.getField(a));
		}
		return values;
	}

	public boolean contains(Attribute a) {
		return attributes.contains(a);
	}

	@Override
	public Iterator<Attribute> iterator() {
		return attributes.iterator();
	}

	public boolean contains(AttributeSet subset) {
		for (Attribute a : subset) {
			if (!contains(a))
				return false;
		}
		return true;
	}

	public AttributeSet union(AttributeSet other) {
		Set<Attribute> union = new HashSet<Attribute>();
		union.addAll(attributes);
		for (Attribute a : other.attributes) {
			if (!union.contains(a))
				union.add(a);
		}
		return new AttributeSet(union);
	}

	public AttributeSet difference(AttributeSet other) {
		Set<Attribute> difference = new HashSet<Attribute>();
		for (Attribute a : attributes) {
			if (!other.contains(a)) {
				difference.add(a);
			}
		}
		return new AttributeSet(difference);
	}

	public AttributeSet intersection(AttributeSet other) {
		Set<Attribute> intersect = new HashSet<Attribute>();
		for (Attribute a : attributes) {
			if (other.contains(a)) {
				intersect.add(a);
			}
		}
		return new AttributeSet(intersect);
	}

	@Override
	public String toString() {
		if (attributes.isEmpty())
			return "{}";
		StringBuilder sb = new StringBuilder("{");
		for (Attribute a : attributes) {
			sb.append(a.getLabel());
			sb.append(",");
		}

		sb.replace(sb.length() - 1, sb.length(), "}");
		return sb.toString();
	}

	public int getSize() {
		return attributes.size();
	}

	public Attribute get(String aname) {
		for (Attribute a : attributes) {
			if (a.getLabel().equalsIgnoreCase(aname)) {
				return a;
			}
		}
		return null;
	}

	public Stream<Attribute> stream() {
		return attributes.stream();
	}

	public Attribute first() {
		return attributes.get(0);
	}
	
	public List<Attribute> getAttributes(){
	  return attributes;
	}
	
	 public void setAttributes(List<Attribute> list){
	    this.attributes = list;
	  }
	 
}
