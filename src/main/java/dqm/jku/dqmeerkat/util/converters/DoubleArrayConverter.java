package dqm.jku.dqmeerkat.util.converters;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;


/**
 * This class converts with its static function {@link #extractNumericAttributesToDoubleArray(RecordList)} the values of a {@link RecordList} into a <code>double[][]</code>.
 *
 * @author Johannes Schrott
 */


public class DoubleArrayConverter {
    public static double[][] extractNumericAttributesToDoubleArray(RecordList rl) {
    	// If the record list is empty we can simply return an empty double[][]
        if (rl.isEmpty() || rl.getAttributes().getSize() == 0 || rl.size() == 0) {
            return new double[0][0];
        }

        double[][] array = new double[rl.size()][rl.getAttributes().getSize()];

		// Iterate over all the values in the recordList and add them to the double[][].
		// Outer Iteration is for records = array rows, inner for attributes = array cols.
        int recordIndex = 0;
        for (Record r : rl) {

            int attributeIndex = 0;
            for (Attribute a : r.getFields()) {
                if (a.hasNumericDataType()) {
                    if (r.getField(a) != null) {
                        array[recordIndex][attributeIndex] = ((Number) r.getField(a)).doubleValue();
                    } else array[recordIndex][attributeIndex] = (Double.NaN);
                } else if (a.getDataType() == String.class)
					array[recordIndex][attributeIndex] = ((Number) ((String) r.getField(a)).length()).doubleValue(); // String is represented as a number of its length
                attributeIndex++;
            }
            recordIndex++;
        }

        return array;
    }
}
