package dqm.jku.trustkg.dsd.records;

import java.util.Iterator;
import java.util.Map.Entry;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.util.AttributeSet;
import dqm.jku.trustkg.util.DataTypeConverter;

import java.util.SortedMap;
import java.util.TreeMap;

public class Record implements Comparable<Record> {

	private SortedMap<Attribute, Object> values;
	public final Concept assignedFrom;

	public Record(Concept assignedFrom) {
		this.assignedFrom = assignedFrom;
		values = new TreeMap<Attribute, Object>();
	}
	
	public void addValueFromCSV(Attribute attribute, String string) {
		addValue(attribute, DataTypeConverter.getDataValueFromCSV(attribute, string));
	}
	
	public void addValue(Attribute attribute, Object o) {
		if (!attribute.getDataType().isInstance(o) && (!attribute.isNullable() && o == null))
			throw new IllegalArgumentException("Attribute Type " + attribute.getDataType() + " does not allow Value " + o);

		if (values.containsKey(attribute))
			throw new IllegalArgumentException("Override in Record");
		values.put(attribute, o);
	}

	public Object getField(Attribute attribute) {
		return values.get(attribute);
	}

	public Object getField(String attributename) {
		for (Attribute a : values.keySet()) {
			if (a.getLabel().equals(attributename)) {
				return getField(a);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Object o : values.values()) {
			sb.append(o == null ? "null" : o.toString());
			sb.append(" | ");
		}
		return sb.substring(0, sb.length() - 3);
	}

	public String toString(Iterable<Attribute> order) {
		StringBuilder sb = new StringBuilder();
		for (Attribute a : order) {
			Object o = getField(a);
			sb.append(o == null ? "null" : o.toString());
			sb.append(" | ");
		}
		return sb.substring(0, sb.length() - 3);
	}

	@Override
	public int compareTo(Record other) {
		return this.toString().compareTo(other.toString());
	}

	@Override
	public int hashCode() {
		int sum = 0;
		for (Attribute a : values.keySet()) {
			sum += a.hashCode();
		}
		for (Object o : values.values()) {
			sum += o == null ? 0 : o.hashCode();
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
		Record other = (Record) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		}
		if (values.size() != other.values.size())
			return false;

		Iterator<Entry<Attribute, Object>> iter = values.entrySet().iterator();
		Iterator<Entry<Attribute, Object>> otherIter = other.values.entrySet().iterator();
		if (iter == null)
			return otherIter == null;

		while (iter.hasNext()) {
			Entry<Attribute, Object> e1 = iter.next();
			Entry<Attribute, Object> e2 = otherIter.next();
			if (e1 == null)
				return e2 == null;

			if (!e1.getKey().equals(e2.getKey())) {
				return false;
			}
			if (e1.getValue() == null) {
				return e2.getValue() == null;
			}
			if (!e1.getValue().equals(e2.getValue())) {
				return false;
			}
		}
		return true;
	}

	public AttributeSet getFields() {
		return new AttributeSet(values.keySet());
	}
}
