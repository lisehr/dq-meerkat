package dqm.jku.dqmeerkat.dsd.integrationOperators;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.DSDFactory;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.integrated.IntegratedConcept;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.util.AttributeSet;

public class Identity extends Operator {

	private final Concept content;
	private final DSConnector conn;
	private final HashMap<Attribute, Attribute> attributeAssignment = new HashMap<Attribute, Attribute>();

	public Identity(IntegratedConcept iconcept, Concept content, DSConnector conn) {
		super(iconcept);
		this.content = content;
		this.conn = conn;

		for (Attribute a : content.getAttributes()) {
			Attribute a1 = DSDFactory.makeBlindAttribute(a.getLabel(), getIconcept());
			a.duplicate(a1);
			attributeAssignment.put(a1, a);
		}

	}

	@Override
	public AttributeSet getAttributes() {
		return new AttributeSet(attributeAssignment.keySet());
	}

	@Override
	public Iterable<Record> getRecords() {
		final Concept iconcept = this.iconcept;

		try {
			final Iterator<Record> iter = conn.getRecords(content);
			return new Iterable<Record>() {

				@Override
				public Iterator<Record> iterator() {
					return new Iterator<Record>() {

						Iterator<Record> iterator = iter;

						@Override
						public boolean hasNext() {
							return iterator.hasNext();
						}

						@Override
						public Record next() {
							if (!iterator.hasNext())
								return null;
							Record res = new Record(iconcept);
							Record old = iterator.next();
							for (Attribute a : attributeAssignment.keySet()) {
								res.addValue(a, old.getField(attributeAssignment.get(a)));
							}
							return res;
						}
					};
				}
			};

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Set<Concept> getDependendConcepts() {
		Set<Concept> concepts = new HashSet<Concept>();
		concepts.add(content);
		return concepts;
	}

	@Override
	public Set<Datasource> getDependendDatasources() {
		Set<Datasource> dss = new HashSet<Datasource>();
		dss.add(content.getDatasource());
		return dss;
	}

	public Concept getContent() {
		return content;
	}

	@Override
	public int getNrRecords() {
		try {
			return conn.getNrRecords(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
