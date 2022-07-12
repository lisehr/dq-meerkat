package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.graphCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numEntries;

public class NumEntries extends AbstractProfileStatistic {


    public NumEntries(DataProfile d) {
        super(numEntries, graphCat, d);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        super.setValue(rs.size());
        super.setNumericVal(((Number) rs.size()).longValue());
        super.setValueClass(Integer.class);
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {

    }

    @Override
    public void update(RecordList rs) {

    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Object> m, double threshold) {        // Never evaluates to false, because the reference is here the size of the RDP and should not be compared to the batch size of the DPs
        return true;
    }
}
