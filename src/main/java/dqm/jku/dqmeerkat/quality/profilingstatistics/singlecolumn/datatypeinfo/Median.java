package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.NumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.ArrayList;
import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.med;


/**
 * Describes the metric Median, which is the middle value of the sorted values
 * of the Attribute. Does not always have to be the average value.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Median")
public class Median extends NumberProfileStatistic<Double, Double> {

    public Median(DataProfile d) {
        super(med, dti, d, Double.class);
    }

    @Override
    public void calculation(RecordList rs, Double oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        List<Number> list = new ArrayList<Number>();
        if (ensureDataTypeCorrect(a.getDataType())) {
            for (Record r : rs) {
                Number field;
                // TODO implement for Strings
//            if (a.getDataType().equals(String.class) && r.getField(a) != null)
//                field = ((String) r.getField(a)).length();
                field = (Number) r.getField(a);
                if (field != null) {
                    list.add(field);
                }
            }
        }
        list.sort(new NumberComparator());
        var val = getMedian(list, list.size());
        this.setValue(val);
    }

    /**
     * Method for getting the median of a list of data
     *
     * @param list the list of data (sorted in ascending order)
     * @param size the size of records
     * @return the median of the list
     */
    private double getMedian(List<Number> list, int size) {
        boolean isEven = false;
        if (list.size() < size || list.isEmpty()) {
            return Double.MIN_VALUE;
        }
        if (size % 2 == 0) {
            isEven = true;
        }
        size /= 2;
        Number val = list.get(size);
        if (isEven) {
            if (size == 1) {
                val = averageResult(val, list.get(0));
            } else {
                val = averageResult(val, list.get(size + 1));
            }
        }
        return val.doubleValue();
    }

    /**
     * Method for averaging the result, used if the number of measured records is
     * even
     *
     * @param oddMedian the median value if the amount of records is odd
     * @param next      the follow up value
     * @return the weighted median
     */
    private Number averageResult(Number oddMedian, Number next) {
        return ((oddMedian.doubleValue() + next.doubleValue()) / 2);
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, super.getValue());
    }


    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Double, Double> m, double threshold) {
        if (this.getValue() == null) {
            setValue(m.getValue());
        }
        double rdpVal = ((Number) this.getValue()).doubleValue();
        double dpValue = ((Number) m.getValue()).doubleValue();

        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG) {
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        }
        return conf;
    }
}
