package dqm.jku.trustkg.dsd.integrationOperators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.util.AttributeSet;

public class Union extends Operator {

	private final Operator left;
	private final Operator right;

	public Union(Operator left, Operator right) {
		super(left.getIconcept());
		this.left = left;
		this.right = right;
		if (!left.getAttributes().equals(right.getAttributes())) {
			throw new IllegalArgumentException("Union of different AttributeSet impossible");
		}
	}

	@Override
	public AttributeSet getAttributes() {
		return left.getAttributes();
	}

	@Override
	public Iterable<Record> getRecords() {
		return new Iterable<Record>() {
			@Override
			public Iterator<Record> iterator() {
				return new Iterator<Record>() {

					Iterator<Record> leftIt = left.getRecords().iterator();
					Iterator<Record> rightIt = right.getRecords().iterator();

					@Override
					public boolean hasNext() {
						return leftIt.hasNext() || rightIt.hasNext();
					}

					@Override
					public Record next() {
						if (leftIt.hasNext())
							return leftIt.next();
						if (rightIt.hasNext())
							return rightIt.next();
						return null;
					}
				};
			}
		};
	}

	@Override
	public Set<Concept> getDependendConcepts() {
		Set<Concept> concepts = new HashSet<Concept>(right.getDependendConcepts());
		concepts.addAll(left.getDependendConcepts());
		return concepts;
	}

	@Override
	public Set<Datasource> getDependendDatasources() {
		Set<Datasource> dss = new HashSet<Datasource>(left.getDependendDatasources());
		dss.addAll(right.getDependendDatasources());
		return dss;
	}

	public Operator getLeft() {
		return left;
	}

	public Operator getRight() {
		return right;
	}

	@Override
	public int getNrRecords() {
		return left.getNrRecords() + right.getNrRecords();
	}

}
