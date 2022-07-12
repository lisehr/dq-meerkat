package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.util.Constants;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

/**
 * This class computes the standard deviation of a sample. It does not compute the standard deviation of a population.
 */

@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/StandardDeviation")
public class StandardDeviation extends DependentProfileStatistic<Double> {
    public StandardDeviation(DataProfile d) {
        super(sd, dti, d);
    }

    private void calculation(RecordList rl, Double oldVal, boolean checked) {
        if (!checked)
            this.dependencyCalculationWithRecordList(rl);
        var avgVal = (double) super.getRefProf().getStatistic(avg).getValue();
        var val = 0D;
        val = Objects.requireNonNullElse(oldVal, 0D);

        for (Record r : rl) {
            var field = (double) (r.getField((Attribute) super.getRefElem()));
            val = addValue(val, field, avgVal);
        }
        val = performAveraging(val);
        this.setValue(val);
        this.setValueClass(Double.class);
    }

    /**
     * Adds a value to the sum of squared differences
     *
     * @param current the current sum of values
     * @param toAdd   the value to be added
     * @param avg     the average value to substract
     * @return the new sum of values
     */
    private Double addValue(Double current, Double toAdd, Double avg) {
        if (toAdd == null)
            return current;
        Attribute a = (Attribute) super.getRefElem();

//        if (a.getClass().equals(String.class)) // TODO move to string implementation
//            return (int) current + (int) Math.pow(((String) toAdd).length() - ((String) avg).length(), 2);
        return (double) current + Math.pow((((Number) toAdd).doubleValue() - ((Number) avg).doubleValue()), 2);
    }

    /**
     * Method for getting the square root of the average value of the objects
     *
     * @param sum the sum of values
     * @return the square root of the average value
     */
    private Double performAveraging(Double sum) {
        if (((long) super.getRefProf().getStatistic(numrows).getValue()) == 1)
            return sum;
        Attribute a = (Attribute) super.getRefElem();
        return Math.sqrt((sum / ((long) super.getRefProf().getStatistic(numrows).getValue() - 1)));
    }

    @Override
    public void calculation(RecordList rl, Double oldVal) {
        calculation(rl, oldVal, false);
    }


    @Override
    public void update(RecordList rl) {
        calculation(rl, null, true);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

//    @Override
//    protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
//        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows))
//            super.getRefProf().getStatistic(numrows).calculationNumeric(list, null);
//        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(avg))
//            super.getRefProf().getStatistic(avg).calculationNumeric(list, null);
//    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(avg))
            super.getRefProf().getStatistic(avg).calculation(rl, null);
    }

    @Override
    protected void dependencyCheck() {
        var numrowM = super.getRefProf().getStatistic(numrows);
        if (numrowM == null) {
            numrowM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(numrowM);
        }
        var avgM = super.getRefProf().getStatistic(avg);
        if (avgM == null) {
            avgM = new Average(super.getRefProf());
            super.getRefProf().addStatistic(avgM);
        }

    }

    /**
     * Creates a basic instance used as a reference (in this case zero as a number)
     *
     * @return the reference value
     */
    private Object getBasicInstance() {
        Attribute a = (Attribute)
                super.getRefElem();
        if (a.getDataType().equals(Long.class))
            return Long.valueOf(0);
        else if (a.getDataType().equals(Double.class))
            return Double.valueOf(0);
        else return Integer.valueOf(0);
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Double> m, double threshold) {
        if (getValue() == null)
            setValue(m.getValue());
        double rdpVal = ((Number) this.getValue()).doubleValue();
        double dpValue = ((Number) m.getValue()).doubleValue();

        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG)
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        return conf;
    }
}
