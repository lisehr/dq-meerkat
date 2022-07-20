package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.IntegerResultProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import java.util.HashSet;
import java.util.Set;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.graphCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.distinctEntries;


public class DistinctEntries extends IntegerResultProfileStatistic<Integer> {

    public DistinctEntries(DataProfile d) {
        super(distinctEntries, graphCat, d, Integer.class);
    }


    @Override
    public void calculation(RecordList rs, Integer oldVal) {
        Set<Record> distinctEntries = new HashSet<>(rs.toList());

        super.setValue(distinctEntries.size());
        super.setInputValueClass(Integer.class);
    }

    @Override
    public void update(RecordList rs) {

    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Integer, Integer> m, double threshold) {
        return false;
    }
}
