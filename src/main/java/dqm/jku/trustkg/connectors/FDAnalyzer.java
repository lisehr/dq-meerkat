package dqm.jku.trustkg.connectors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dqm.jku.trustkg.dsd.DSDFactory;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.util.AttributeSet;
import dqm.jku.trustkg.util.AttributeValueSet;
import dqm.jku.trustkg.util.sparse.SparseLabeledMatrix;

public class FDAnalyzer {

	private boolean openWorld = false;

	HashMap<AttributeSet, HashMap<Attribute, HashMap<AttributeValueSet, Object>>> cache = new HashMap<AttributeSet, HashMap<Attribute, HashMap<AttributeValueSet, Object>>>();
	SparseLabeledMatrix<AttributeSet, Attribute> violations = new SparseLabeledMatrix<AttributeSet, Attribute>();

	public FDAnalyzer(Set<AttributeSet> lefts, Set<Attribute> rights) {
		for (AttributeSet left : lefts) {
			HashMap<Attribute, HashMap<AttributeValueSet, Object>> leftMap = new HashMap<Attribute, HashMap<AttributeValueSet, Object>>();

			for (Attribute right : rights) {
				HashMap<AttributeValueSet, Object> rightMap = new HashMap<AttributeValueSet, Object>();
				leftMap.put(right, rightMap);
			}
			cache.put(left, leftMap);
		}
	}

	public FDAnalyzer(Set<AttributeSet> lefts, AttributeSet rights) {
		for (AttributeSet left : lefts) {
			HashMap<Attribute, HashMap<AttributeValueSet, Object>> leftMap = new HashMap<Attribute, HashMap<AttributeValueSet, Object>>();

			for (Attribute right : rights) {
				HashMap<AttributeValueSet, Object> rightMap = new HashMap<AttributeValueSet, Object>();
				leftMap.put(right, rightMap);
			}
			cache.put(left, leftMap);
		}
	}

	public void analyze(Iterator<Record> iter, Concept concept) {
		readRecords(iter);
		createFunctionalDependencies(concept);
	}

	private void readRecords(Iterator<Record> iter) {
		while (iter.hasNext()) {
			check(iter.next());
		}
	}

	private void createFunctionalDependencies(Concept concept) {
		for (AttributeSet left : cache.keySet()) {
			for (Attribute right : cache.get(left).keySet()) {
				if (!left.contains(right) && violations.get(left, right) == 0 && left.getSize() != 0)
					DSDFactory.makeFunctionalDependency(left, right, concept);
			}
		}
	}

	private void check(Record r) {
		for (AttributeSet left : cache.keySet()) {
			HashMap<Attribute, HashMap<AttributeValueSet, Object>> leftMap = cache.get(left);

			for (Attribute right : leftMap.keySet()) {
				HashMap<AttributeValueSet, Object> rightMap = leftMap.get(right);
				AttributeValueSet leftVal = left.getAttributeValueSet(r);
				Object rightVal = r.getField(right);

				if (rightVal == null && openWorld)
					continue;

				if (rightMap.containsKey(leftVal)) {
					if ((leftVal == null) && (rightMap.get(leftVal) == null))
						continue;
					if (rightMap.get(leftVal) == null || !rightMap.get(leftVal).equals(rightVal))
						violations.inc(left, right);
				} else {
					rightMap.put(leftVal, rightVal);
				}
			}
		}
	}
}
