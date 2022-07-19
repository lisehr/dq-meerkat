package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentNumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.cardCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;


/**
 * Describes the metric Uniqueness, the Cardinality in relation to the number of
 * rows.
 * The result is a percentage value between 0 and 100.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/cardinality/Uniqueness")
public class Uniqueness extends DependentNumberProfileStatistic<Double, Double> {

    public Uniqueness(DataProfile d) {
        super(unique, cardCat, d, Double.class);
    }

    /**
     * Local variant of calculation to prevent a double check for dependent metrics
     *
     * @param rl      the recordlist
     * @param oldVal  old value of metric
     * @param checked flag for dependency check
     */
    private void calculation(RecordList rl, Double oldVal, boolean checked) {
        if (!checked) dependencyCalculationWithRecordList(rl);
        long cardinality = (long) (super.getRefProf().getStatistic(card).getValue());
        long numRecs = (long) super.getRefProf().getStatistic(numrows).getValue();
        double result = cardinality * 100.0 / numRecs;
        this.setValue(result);
        this.setInputValueClass(Double.class);

    }

    @Override
    public void calculation(RecordList rs, Double oldVal) {
        calculation(rs, null, false);
    }


    @Override
    public void update(RecordList rs) {
        calculation(rs, super.getValue());
    }

    @Override
    protected String getValueString() {
        if (getValue() == null) {
            return "\tnull";
        } else {
            return "\t" + getValue().toString() + "%";
        }
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(unique) - 2 <= super.getMetricPos(numrows)) {
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
        }
        if (super.getMetricPos(unique) - 1 <= super.getMetricPos(card)) {
            super.getRefProf().getStatistic(card).calculation(rl, null);
        }

    }

    @Override
    protected void dependencyCheck() {
        var sizeM = super.getRefProf().getStatistic(numrows);
        if (sizeM == null) {
            sizeM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(sizeM);
        }
        var cardM = super.getRefProf().getStatistic(card);
        if (cardM == null) {
            cardM = new Cardinality(super.getRefProf());
            super.getRefProf().addStatistic(cardM);
        }
    }

    // TODO is this really correct?
    @Override
    public boolean checkConformance(ProfileStatistic<Double, Double> m, double threshold) {        // Excluded: depends on cardinality & num rows (RDP size != DP size)
        return true;
    }

    @Override
    protected Double getBasicInstance() {
        return 0D;
    }
}
