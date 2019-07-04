package dsd.integrationOperators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import dsd.DSDFactory;
import dsd.elements.Attribute;
import dsd.elements.Concept;
import dsd.elements.Datasource;
import dsd.records.Record;
import util.AttributeSet;

public class CrossProduct extends Operator {

	private final Operator left;
	private final Operator right;
	private final HashMap<Attribute, Attribute> attributeAssignment = new HashMap<Attribute, Attribute>();
	private final HashMap<Attribute, Operator> operatorAssignment = new HashMap<Attribute, Operator>();

	public CrossProduct(Operator left, Operator right) {
		super(left.iconcept);
		if (!right.iconcept.equals(iconcept))
			throw new IllegalArgumentException(
					"Crossproduct: left and right operators are not from the same integrated concept. ");
		this.left = left;
		this.right = right;
		AttributeSet inter = left.getAttributes().intersection(right.getAttributes());
		for (Attribute a : inter) {
			Attribute a1 = DSDFactory.makeBlindAttribute(a.getLabel() + "_1", iconcept);
			a.duplicate(a1);
			Attribute a2 = DSDFactory.makeBlindAttribute(a.getLabel() + "_2", iconcept);
			a.duplicate(a2);
			attributeAssignment.put(a1, a);
			attributeAssignment.put(a2, a);
			operatorAssignment.put(a1, left);
			operatorAssignment.put(a2, right);
		}
		for (Attribute a : left.getAttributes()) {
			if (attributeAssignment.containsValue(a))
				continue;
			attributeAssignment.put(a, a);
			operatorAssignment.put(a, left);
		}
		for (Attribute a : right.getAttributes()) {
			if (attributeAssignment.containsValue(a))
				continue;
			attributeAssignment.put(a, a);
			operatorAssignment.put(a, right);
		}

	}

	@Override
	public AttributeSet getAttributes() {
		return new AttributeSet(operatorAssignment.keySet());
	}

	@Override
	public Iterable<Record> getRecords() {
		return new Iterable<Record>() {
			@Override
			public Iterator<Record> iterator() {
				return new Iterator<Record>() {

					Iterator<Record> leftIt = left.getRecords().iterator();
					Iterator<Record> rightIt;
					Record leftcur;
					Record rightcur;

					@Override
					public boolean hasNext() {
						if (leftIt.hasNext()) {
							return true;
						}
						return rightIt.hasNext();
					}

					@Override
					public Record next() {
						if (rightIt != null && rightIt.hasNext()) {
							rightcur = rightIt.next();
							return buildRecord();
						}
						if (leftIt.hasNext()) {
							leftcur = leftIt.next();
							rightIt = right.getRecords().iterator();
							rightcur = rightIt.next();
							return buildRecord();
						}
						return null;
					}

					private Record buildRecord() {
						Record res = new Record(iconcept);

						for (Attribute a : operatorAssignment.keySet()) {
							Object o;
							if (operatorAssignment.get(a).equals(left)) {
								o = leftcur.getField(attributeAssignment.get(a));
							} else {
								o = rightcur.getField(attributeAssignment.get(a));
							}
							res.addValue(a, o);
						}

						return res;
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
		return left.getNrRecords() * right.getNrRecords();
	}

}
