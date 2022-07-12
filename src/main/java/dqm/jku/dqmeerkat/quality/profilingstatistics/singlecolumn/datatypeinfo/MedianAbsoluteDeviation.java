package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/MedianAbsoluteDeviation")
public class MedianAbsoluteDeviation extends DependentProfileStatistic {

    public MedianAbsoluteDeviation(DataProfile d) {
        super(mad, dti, d);
    }

    private void calculation(RecordList rl, Object oldVal, boolean checked) {
        if (!checked) this.dependencyCalculationWithRecordList(rl);
        Object medVal = super.getRefProf().getStatistic(med).getValue();
        List<Number> medians = new ArrayList<>();
        for (Record r : rl) {
            Object field = r.getField((Attribute) super.getRefElem());
            medians.add((Number) subValue(field, medVal));
        }
        Median medM = new Median(this.getRefProf());
        medM.calculationNumeric(medians, null);
        Object med = medM.getValue();
        this.setValue(med);
        this.setNumericVal(((Number) med).doubleValue());
        this.setValueClass(Double.class);
    }

    /**
     * Adds a value to the sum of squared differences
     *
     * @param toAdd   the value to be added
     * @return the new sum of values
     */
    private Object subValue(Object toAdd, Object med) {
        if (toAdd == null) return 0;
        Attribute a = (Attribute) super.getRefElem();
        if (a.getDataType().equals(Long.class)) return (long) toAdd - (long) med;
        else if (a.getDataType().equals(Double.class))
            return ((Number) toAdd).doubleValue() - ((Number) med).doubleValue();
        else if (a.getClass().equals(String.class)) return (int) toAdd - (int) med;
        else return (intToBigInteger(toAdd).subtract(intToBigInteger(med)));
    }

    private BigInteger intToBigInteger(Object i) {
        if (i.getClass().equals(BigInteger.class)) return (BigInteger) i;
        return BigInteger.valueOf((int) i);
    }

    @Override
    public void calculation(RecordList rl, Object oldVal) {
        calculation(rl, oldVal, false);
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
        this.dependencyCalculationWithNumericList(list);
        if (list == null || list.isEmpty()) {
            if (oldVal != null) return;
            else this.setValue(null);
        } else {
            Object medVal = super.getRefProf().getStatistic(med).getValue();
            list.sort(new NumberComparator());
            List<Number> medians = new ArrayList<>();
            for (Number n : list) {
                if (new BigDecimal(n.toString()).compareTo(new BigDecimal(medVal.toString())) == -1)
                    medians.add((Number) subValue(medVal, n));
                else medians.add((Number) subValue(n, medVal));
            }
            Median medM = new Median(this.getRefProf());
            medM.calculationNumeric(medians, null);
            Object med = medM.getValue();
            this.setValue(med);
            this.setNumericVal(((Number) med).doubleValue());
        }
        this.setValueClass(Double.class);
    }

    @Override
    public void update(RecordList rl) {
        calculation(rl, null, true);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculationNumeric(list, null);
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(med))
            super.getRefProf().getStatistic(med).calculationNumeric(list, null);
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(med))
            super.getRefProf().getStatistic(med).calculation(rl, null);
    }

    @Override
    protected void dependencyCheck() {
        var numrowM = super.getRefProf().getStatistic(numrows);
        if (numrowM == null) {
            numrowM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(numrowM);
        }
        var medM = super.getRefProf().getStatistic(med);
        if (medM == null) {
            medM = new Median(super.getRefProf());
            super.getRefProf().addStatistic(medM);
        }

    }

    @Override
    public boolean checkConformance(ProfileStatistic<Object> m, double threshold) {
        if (getNumericVal() == null)
            setNumericVal(m.getNumericVal());
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
