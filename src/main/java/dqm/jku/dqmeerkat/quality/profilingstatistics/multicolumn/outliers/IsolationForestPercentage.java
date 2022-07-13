package dqm.jku.dqmeerkat.quality.profilingstatistics.multicolumn.outliers;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentNumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;

import java.util.ArrayList;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.out;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.isoF;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.isoFP;

public class IsolationForestPercentage extends DependentNumberProfileStatistic<Double> {

    public IsolationForestPercentage(DataProfile dp) {
        super(isoFP, out, dp);
    }


    @Override
    public void calculation(RecordList rs, Double oldVal) {
        calculation(rs, oldVal, false);
    }

    private void calculation(RecordList rl, Object oldVal, boolean checked) {
        if (!checked) dependencyCalculationWithRecordList(rl);
        int size = ((ArrayList<?>) super.getRefProf().getStatistic(isoF).getValue()).size();
        int numRecs = rl.size();
        double result = size * 100.0 / numRecs;
        this.setValue(result);
        this.setValueClass(Double.class);

    }


    @Override
    public void update(RecordList rs) {
        calculation(rs, null, false);
    }

    @Override
    protected String getValueString() {
        if (getValue() == null) return "\tnull";
        else return "\t" + getValue().toString() + "%";
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(StatisticTitle.isoFP) - 1 <= super.getMetricPos(isoF))
            super.getRefProf().getStatistic(isoF).calculation(rl, null);
    }

    @Override
    protected void dependencyCheck() {
        var isoM = super.getRefProf().getStatistic(isoF);
        if (isoM == null || isoM.getValue() == null) {
            isoM = new IsolationForest(super.getRefProf());
            super.getRefProf().addStatistic(isoM);
        }
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Double> m, double threshold) {        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected Double getBasicInstance() {
        return 0D;
    }
}
