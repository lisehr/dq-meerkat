package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.NumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.graphCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numEntries;

public class NumEntries extends NumberProfileStatistic<Integer> {


    public NumEntries(DataProfile d) {
        super(numEntries, graphCat, d, Integer.class);
    }

    @Override
    public void calculation(RecordList rs, Integer oldVal) {
        super.setValue(rs.size());
        super.setValueClass(Integer.class);
    }


    @Override
    public void update(RecordList rs) {
        calculation(rs, value);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Integer> m, double threshold) {        // Never evaluates to false, because the reference is here the size of the RDP and should not be compared to the batch size of the DPs
        return true;
    }
}
