package dqm.jku.trustkg.quality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dqm.jku.trustkg.dsd.elements.ConceptConstraint;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;

public class DataQualityRecords implements Serializable {

	private static final long serialVersionUID = 1L;
	private HashMap<ConceptConstraint, List<Record>> violationRecords = new HashMap<ConceptConstraint, List<Record>>();
	private HashMap<DSDElement, List<RecordList>> duplicateRecords = new HashMap<DSDElement, List<RecordList>>();

	public void addViolationRecord(ConceptConstraint cc, Record r) {
		if (!violationRecords.containsKey(cc)) {
			violationRecords.put(cc, new ArrayList<Record>());
		}
		violationRecords.get(cc).add(r);
	}

	public List<Record> getViolationRecords(ConceptConstraint cc) {
		if (!violationRecords.containsKey(cc)) {
			return new ArrayList<Record>();
		}
		return Collections.unmodifiableList(violationRecords.get(cc));
	}

	public void addDuplicates(DSDElement elem, RecordList set) {
		if (!duplicateRecords.containsKey(elem)) {
			duplicateRecords.put(elem, new ArrayList<RecordList>());
		}
		duplicateRecords.get(elem).add(set);
	}

	public Map<DSDElement, List<RecordList>> getDuplicateRecords() {
		return Collections.unmodifiableMap(duplicateRecords);
	}

	public List<RecordList> getDuplicateRecords(DSDElement elem) {
		return duplicateRecords.get(elem);
	}

}
