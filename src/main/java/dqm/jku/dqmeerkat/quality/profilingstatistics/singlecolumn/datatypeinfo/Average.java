package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;


/**
 * Describes the metric Average, where the average of all values in an Attribute
 * is taken.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Average")
public class Average extends DependentProfileStatistic {
    public Average() {

    }

    public Average(DataProfile d) {
        super(avg, dti, d);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        this.dependencyCalculationWithRecordList(rs);
        Object val = null;
        if (oldVal == null) val = getBasicInstance();
        else val = oldVal;
        for (Record r : rs) {
            Object field = r.getField((Attribute) super.getRefElem());
            val = addValue(val, field);
        }
        val = performAveraging(val);
        this.setValue(val);
        this.setNumericVal(((Number) val).doubleValue());
        this.setValueClass(((Attribute) super.getRefElem()).getDataType());
    }

    /**
     * Method for getting the average value of the objects
     *
     * @param sum the sum of values
     * @return the average value
     */
    private Object performAveraging(Object sum) {
        Attribute a = (Attribute) super.getRefElem();
        long numRows = (long) super.getRefProf().getStatistic(numrows).getValue();
        int scale = 3; // Nachkommastellen für Dezimal Division

        if (a.getDataType().equals(Long.class)) {
            return BigInteger.valueOf((long) sum).divide(BigInteger.valueOf(numRows)).longValue();
            //  return (long) sum / (int) super.getRefProf().getMetric(numrows).getValue();
        } else if (a.getDataType().equals(Double.class)) {
            return BigDecimal.valueOf((double) sum).divide(BigDecimal.valueOf(numRows), scale, RoundingMode.HALF_UP).doubleValue(); // ab 0.5 aufrunden!

            //  return (double) sum / (int) super.getRefProf().getMetric(numrows).getValue();
        }
        return (int) sum / (long) super.getRefProf().getStatistic(numrows).getValue();
    }

    /**
     * Creates a basic instance used as a reference (in this case zero as a number)
     *
     * @return the reference value
     */
    private Object getBasicInstance() {
        Attribute a = (Attribute) super.getRefElem();
        if (a.getDataType().equals(Long.class)) return Long.valueOf(0);
        else if (a.getDataType().equals(Double.class)) return Double.valueOf(0);
        else return Integer.valueOf(0);
    }

    /**
     * Adds a value to the sum of values
     *
     * @param current the current sum of values
     * @param toAdd   the value to be added
     * @return the new sum of values
     */
    private Object addValue(Object current, Object toAdd) {
        if (toAdd == null) return current;
        Attribute a = (Attribute) super.getRefElem();
        if (a.getDataType().equals(Long.class)) return (long) current + ((Number) toAdd).longValue();
        else if (a.getDataType().equals(Double.class)) return (double) current + ((Number) toAdd).doubleValue();
        else if (toAdd.getClass().equals(String.class)) return (int) current + ((String) toAdd).length();
        else return (int) current + (int) toAdd;
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, getOriginalSum());
    }

    /**
     * Restores the original sum of the averaging value
     *
     * @return the sum before weighting with the amount of values
     */
    private Object getOriginalSum() {
        Attribute a = (Attribute) super.getRefElem();
        if (a.getDataType().equals(Long.class))
            return ((Number) super.getValue()).longValue() * (long) super.getRefProf().getStatistic(numrows).getValue();
        else if (a.getDataType().equals(Double.class))
            return ((Number) super.getValue()).doubleValue() * (long) super.getRefProf().getStatistic(numrows).getValue();
        else return ((int) super.getValue()) * (long) super.getRefProf().getStatistic(numrows).getValue();
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
        this.dependencyCalculationWithNumericList(list);
        if (list == null || list.isEmpty()) {
            if (oldVal != null) return;
            else this.setValue(null);
        } else {
            list.sort(new NumberComparator());
            Object sum = null;
            if (oldVal == null) sum = getBasicInstance();
            else sum = getOriginalSum();
            for (Number n : list) {
                sum = addValue(sum, n);
            }
            sum = performAveraging(sum);
            this.setValue(sum);
            this.setNumericVal(((Number) sum).doubleValue());
        }
        Attribute a = (Attribute) super.getRefElem();
        this.setValueClass(a.getDataType());
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
        if (super.getMetricPos(avg) - 1 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculationNumeric(list, null);
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(avg) - 2 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
    }

    @Override
    protected void dependencyCheck() {
        ProfileStatistic sizeM = super.getRefProf().getStatistic(numrows);
        if (sizeM == null) {
            sizeM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(sizeM);
        }
    }

    @Override
    public boolean checkConformance(ProfileStatistic m, double threshold) {
        if (this.getNumericVal() == null)
            this.setNumericVal(m.getNumericVal());
        double rdpVal = ((Number) this.getNumericVal()).doubleValue();
        double dpValue = ((Number) m.getValue()).doubleValue();

        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG)
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        return conf;
    }
}
