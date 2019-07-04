package dsd.integrationOperators;

import java.util.Iterator;
import java.util.Set;

import dsd.elements.Attribute;
import dsd.elements.Concept;
import dsd.elements.Datasource;
import dsd.records.Record;
import util.AttributeSet;

public class Projection extends Operator {

	private final Operator from;
	private final AttributeSet attributes;

	public Projection(Operator from, AttributeSet attributes) {
		super(from.iconcept);
		this.from = from;
		this.attributes = attributes;
	}

	@Override
	public AttributeSet getAttributes() {
		return attributes;
	}

	@Override
	public Iterable<Record> getRecords() {
		return new Iterable<Record>() {

			@Override
			public Iterator<Record> iterator() {
				return new Iterator<Record>() {
					Iterator<Record> iterator = from.getRecords().iterator();

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public Record next() {
						Record res = new Record(iconcept);
						Record old = iterator.next();
						for (Attribute a : attributes) {
							res.addValue(a, old.getField(a));
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
