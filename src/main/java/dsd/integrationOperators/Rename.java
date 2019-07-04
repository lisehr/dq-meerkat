package dsd.integrationOperators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import dsd.DSDFactory;
import dsd.elements.Attribute;
import dsd.elements.Concept;
import dsd.elements.Datasource;
import dsd.records.Record;
import util.AttributeSet;

public class Rename extends Operator {

	private final Operator from;
	private final Map<Attribute, Attribute> attributeAssignment = new HashMap<Attribute, Attribute>();

	public Rename(Operator from, Map<String, String> renaming) {
		super(from.iconcept);
		this.from = from;

		for (Attribute a : from.getAttributes()) {
			if (renaming.containsKey(a.getLabel())) {
				Attribute a1 = DSDFactory.makeBlindAttribute(renaming.get(a.getLabel()), getIconcept());
				a.duplicate(a1);
				attributeAssignment.put(a, a1);
			} else {
				attributeAssignment.put(a, a);
			}
		}
	}

	@Override
	public AttributeSet getAttributes() {
		return new AttributeSet(attributeAssignment.values());
	}

	@Override
	public Iterable<Record> getRecords() {
		return new Iterable<Record>() {
			@Override
			public Iterator<Record> iterator() {
				return new Iterator<Record>() {

					private final Iterator<Record> iterator = from.getRecords().iterator();

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public Record next() {
						Record old = iterator.next();
						Record res = new Record(iconcept);
						for (Attribute a : attributeAssignment.keySet()) {
							res.addValue(attributeAssignment.get(a), old.getField(a));
						}
						return res;
					}
				};
			}
		};
	}

	@Override
	public Set<Concept> getDependendConcepts() {
		return from.getDependendConcepts();
	}

	@Override
	public Set<Datasource> getDependendDatasources() {
		return from.getDependendDatasources();
	}

	public Operator getFrom() {
		return from;
	}

	@Override
	public int getNrRecords() {
		return from.getNrRecords();
	}

}
