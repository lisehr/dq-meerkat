package dqm.jku.dqmeerkat.dsd.integrationOperators;

import java.util.Iterator;
import java.util.Set;

import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.util.AttributeSet;
import dqm.jku.dqmeerkat.util.validators.Validator;

public class Selection extends Operator {

	private final Operator from;
	private final Validator where;
	private double p;
	private int size;
	private int effect;

	public Selection(Operator from, Validator where) {
		super(from.getIconcept());
		this.from = from;
		this.where = where;
	}

	@Override
	public AttributeSet getAttributes() {
		return from.getAttributes();
	}

	@Override
	public Iterable<Record> getRecords() {
		if (p == 0) {
			size = 0;
			effect = 0;

		}
		return new Iterable<Record>() {
			@Override
			public Iterator<Record> iterator() {
				return new Iterator<Record>() {
					private final Iterator<Record> iterator = from.getRecords().iterator();
					private final Validator val = where;
					Record cur = null;

					@Override
					public boolean hasNext() {
						do {
							if (cur != null && val.validate(cur))
								return true;
							if (!iterator.hasNext()) {
								p = effect / (double) (size);
								return false;
							}
							cur = iterator.next();
							size++;
						} while (true);
					}

					@Override
					public Record next() {
						effect++;
						if (cur == null) {
							hasNext();
						}
						Record res = cur;

						if (!iterator.hasNext()) {
							cur = null;
						} else {
							if (iterator.hasNext()) {
								cur = iterator.next();
							} else {
								cur = null;
							}

							hasNext();
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
		if (size != 0)
			return size;
		int i = 0;
		for (@SuppressWarnings("unused")
		Record r : this.getRecords()) {
			i++;
		}
		return i;
	}

	public double getPercentage() {
		if (p != 0)
			return p;
		for (@SuppressWarnings("unused")
		Record r : this.getRecords()) {
		}
		return p;
	}
}
