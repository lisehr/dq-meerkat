package dqm.jku.dqmeerkat.pentaho.util;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.bt;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.dec;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.dig;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.dt;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.hist;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numrows;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.pattern;

import java.util.ArrayList;
import java.util.List;

import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.step.BaseStep;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.util.converters.DataTypeConverter;

public class PentahoRowUtils {
	
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
	
	public static Record generateRecordFromFirstConceptObjectRow(Datasource conn, Object[] row, boolean first, BaseStep bs, boolean debug) {
		Concept target = null;
		for (Concept c : conn.getConcepts()) {
			target = c;
			break;
		}
		Record r = new Record(target);
		List<Attribute> attributes = target.getSortedAttributes();
		for (int j = 0; j < Math.min(row.length, attributes.size()); j++) {
			if (first) { 				
				if (bs != null && debug) bs.logBasic(attributes.get(j).toString() + " " + attributes.get(j).getDataTypeString() + " " + row[j]);
			}
			r.addValue(attributes.get(j), row[j]);
		}		
		return r;
		
	}
	
	public static List<ValueMetaInterface> createPentahoOutputMeta() throws KettleValueException {
		DataProfile dp = new DataProfile();
		Attribute attribute = new Attribute();
		attribute.setDataType(Integer.class);
		dp.setElem(attribute);
		dp.createDataProfileSkeletonRDB();
		List<ValueMetaInterface> list = new ArrayList<>();
		list.add(new ValueMetaString("URI"));
		for (var m : dp.getStatistics()) {
			if (m.getTitle() != pattern) {
				if ((m.getCat() == StatisticCategory.dti && (m.getTitle() == bt || m.getTitle() == dt)) || m.getTitle() == hist) list.add(new ValueMetaString(m.getLabel()));
				else if (dp.profileStatisticIsNumeric(m)) list.add(new ValueMetaNumber(m.getLabel()));
				else if (m.getCat() == StatisticCategory.depend) list.add(new ValueMetaBoolean(m.getLabel()));
				else list.add(new ValueMetaInteger(m.getLabel()));
			}
		}
		return list;
	}

	public static Object[] getPentahoOutputRowData(RowMetaInterface rowMeta, DataProfile dp) {
		Object[] objects = new Object[rowMeta.size()];
		objects[0] = dp.getURI();
		for (int i = 1; i < rowMeta.size(); i++) dp.getStatistics().get(i - 1);
		return objects;
	}
	

	public static List<ValueMetaAndData> createPentahoOutputRowMeta(DataProfile dp) throws KettleValueException {
		List<ValueMetaAndData> list = new ArrayList<>();
		list.add(new ValueMetaAndData("URI", dp.getElem().getURI()));
		for (var profileMetric : dp.getStatistics()) if (profileMetric.getTitle() != pattern) {
			if (dp.profileStatisticIsNumeric(profileMetric) || profileMetric.getTitle() == dec || profileMetric.getTitle() == dig || profileMetric.getTitle() == numrows) list.add(new ValueMetaAndData(profileMetric.getLabel(), profileMetric.getNumericVal()));
			else if (profileMetric.getTitle() == hist) list.add(new ValueMetaAndData(profileMetric.getLabel(), profileMetric.toString().substring(10, profileMetric.toString().length())));
			else list.add(new ValueMetaAndData(profileMetric.getLabel(), profileMetric.getValue()));			
		}
		return list;
	}



}
