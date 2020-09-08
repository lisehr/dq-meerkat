package dqm.jku.trustkg.util;

import java.util.List;

import org.pentaho.di.trans.step.BaseStep;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;

public class RecordGenUtils {
	
	public static Record generateRecordFromFirstConceptObjectRow(Datasource conn, String objects, boolean first, BaseStep bs) {
		Concept target = null;
		for (Concept c : conn.getConcepts()) {
			target = c;
			break;
		}
		objects = objects.replace("]", "").replace("[", "").replace(", ", ",");
		String[] values = objects.split(",");
		List<Attribute> attributes = target.getSortedAttributes();
		if (first) {
			bs.logBasic(values.length + " " + attributes.size());
			for (int j = 0; j < Math.min(values.length, attributes.size()); j++) {
				DataTypeConverter.getDataTypeFromCSVRecord(attributes.get(j), values[j]);
				if (bs != null) {
					bs.logBasic(attributes.get(j).getDataTypeString() + " " + values[j]);
				}
			}
		}
		Record r = new Record(target);
		for (int j = 0; j < Math.min(values.length, attributes.size()); j++) {
			r.addValueFromCSV(attributes.get(j), values[j]);
		}
		return r;
	}
	
	public static Record generateRecordFromFirstConceptObjectRow(Datasource conn, Object[] row, boolean first, BaseStep bs) {
		Concept target = null;
		for (Concept c : conn.getConcepts()) {
			target = c;
			break;
		}
		Record r = new Record(target);
		List<Attribute> attributes = target.getSortedAttributes();
		for (int j = 0; j < Math.min(row.length, attributes.size()); j++) {
			if (first) { 				
				if (bs != null) bs.logBasic(attributes.get(j).toString() + " " + attributes.get(j).getDataTypeString() + " " + row[j]);
			}
			r.addValue(attributes.get(j), row[j]);
		}		
		return r;
		
	}

}
