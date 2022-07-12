package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.cardCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;


/**
 * Describes the metric Null Values Percentage, which is the amount of Null
 * Values in relation to the data set size.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/cardinality/NullValuesPercentage")
public class NullValuesPercentage extends DependentProfileStatistic {

    public NullValuesPercentage(DataProfile d) {
        super(nullValP, cardCat, d);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        calculation(rs, oldVal, false);
    }

    /**
     * Local variant of calculation to prevent a double check for dependent metrics
     *
     * @param rl      the recordlist
     * @param oldVal  old value of metric
     * @param checked flag for dependency check
     */
    private void calculation(RecordList rl, Object oldVal, boolean checked) {
        if (!checked) dependencyCalculationWithRecordList(rl);
        long nominator = (long) super.getRefProf().getStatistic(nullVal).getValue();
        long denominator = (long) super.getRefProf().getStatistic(numrows).getValue();
        double result;
        if (denominator == 0) result = Double.valueOf(0);
        result = (double) nominator * 100.0 / (double) denominator;
        super.setValue(result);
        super.setNumericVal((Number) result);
        this.setValueClass(Double.class);
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
        dependencyCalculationWithNumericList(list);
        calculation(null, null, true);
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, null);
    }

    @Override
    protected String getValueString() {
        if (getValue() == null) return "\tnull";
        else return "\t" + getValue().toString() + "%";
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(nullValP) - 2 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
        if (super.getMetricPos(nullValP) - 1 <= super.getMetricPos(nullVal))
            super.getRefProf().getStatistic(nullVal).calculation(rl, null);

    }

    @Override
    protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
        if (super.getMetricPos(nullValP) - 2 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculationNumeric(list, null);
        if (super.getMetricPos(nullValP) - 1 <= super.getMetricPos(nullVal))
            super.getRefProf().getStatistic(nullVal).calculationNumeric(list, null);
    }

    @Override
    protected void dependencyCheck() {
        var sizeM = super.getRefProf().getStatistic(numrows);
        if (sizeM == null) {
            sizeM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(sizeM);
        }
        var nullV = super.getRefProf().getStatistic(nullVal);
        if (nullV == null) {
            nullV = new NullValues(super.getRefProf());
            super.getRefProf().addStatistic(nullV);
        }
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Object> m, double threshold) {        // Excluded: depends on cardinality & num rows (RDP size != DP size)
        return true;
    }
}
