package dqm.jku.trustkg.util.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import jep.NDArray;

public class NDArrayConverter {
	public static NDArray<double[]> extractNumericFeaturesFromRecordList(RecordList rl){
		int noExtractedFeatures = 0;
		boolean inited = false;
		ArrayList<Double> dataList = new ArrayList<>();
		HashMap<Attribute, ArrayList<Double>> allVals = new HashMap<>();
		for (Attribute attribute : rl.getAttributes()) {
			allVals.put(attribute, new ArrayList<>());
		}
		for (Record r : rl) {
			if (!inited) {
				noExtractedFeatures = r.getFields().getSize();
				inited = true;
			}
			for (Attribute a : r.getFields()) {
				ArrayList<Double> attrValList = allVals.get(a);
				if (a.hasNumericDataType()) {
					if (r.getField(a) != null) attrValList.add(((Number) r.getField(a)).doubleValue());
					else attrValList.add(Double.NaN);
				}
				else if (a.getDataType() == String.class) attrValList.add(((Number)((String) r.getField(a)).length()).doubleValue());
				allVals.put(a, attrValList);
			}
		}
		for (ArrayList<Double> vals : allVals.values()) dataList.addAll(vals);
		return new NDArray<double[]>(Arrays.stream(dataList.toArray(new Double[0])).mapToDouble(Double::doubleValue).toArray(), rl.size(), noExtractedFeatures);
	}
}
