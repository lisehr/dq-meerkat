package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.IntegerResultProfileStatistic;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.graphCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numEntries;

public class NumEntries extends IntegerResultProfileStatistic<Integer> {


    public NumEntries(DataProfile d) {
        super(numEntries, graphCat, d, Integer.class);
    }

    @Override
    public void calculation(RecordList rs, Integer oldVal) {
        super.setValue(rs.size());
        super.setInputValueClass(Integer.class);
    }


    @Override
    public void update(RecordList rs) {
        calculation(rs, value);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

}
