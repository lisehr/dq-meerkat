package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.dependency;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.Uniqueness;
import dqm.jku.dqmeerkat.util.Constants;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.depend;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.keyCand;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.unique;


/**
 * Describes the metric of a key candidate. This is only the case, if the
 * Uniqueness is at 100%.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dependency/KeyCandidate")
public class KeyCandidate extends DependentProfileStatistic<Boolean> {

    public KeyCandidate(DataProfile d) {
        super(keyCand, depend, d);
    }

    /**
     * Local variant of calculation to prevent a double check for dependent metrics
     *
     * @param rl      the recordlist
     * @param checked flag for dependency check
     */
    private void calculation(RecordList rl, boolean checked) {
        if (!checked) this.dependencyCalculationWithRecordList(rl);
        boolean isKeyCandidate = ((double) this.getRefProf().getStatistic(unique).getValue()) == (double) 100.0;
        super.setValue(isKeyCandidate);

        super.setValueClass(Boolean.class);
    }

    @Override
    public void calculation(RecordList rs, Boolean oldVal) {
        calculation(rs, false);
    }


    @Override
    public void update(RecordList rs) {
        calculation(null, null);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(keyCand) - 1 <= super.getMetricPos(unique))
            super.getRefProf().getStatistic(unique).calculation(rl, null);
    }

    @Override
    protected void dependencyCheck() {
        var uniqueM = super.getRefProf().getStatistic(unique);
        if (uniqueM == null) {
            uniqueM = new Uniqueness(super.getRefProf());
            super.getRefProf().addStatistic(uniqueM);
        }
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Boolean> m, double threshold) {
        String rdpVal = this.getSimpleValueString();
        String dpValue = this.getSimpleValueString();

        boolean conf = rdpVal.equals(dpValue);
        if (!conf && Constants.DEBUG) System.out.println(this.getTitle() + " exceeded: " + dpValue + " != " + rdpVal);
        return conf;
    }
}
