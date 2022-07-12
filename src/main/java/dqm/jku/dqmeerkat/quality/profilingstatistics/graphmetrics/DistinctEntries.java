package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;

import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;


public class DistinctEntries extends AbstractProfileStatistic {

    public DistinctEntries(DataProfile d) {
        super(distinctEntries, graphCat, d);
    }


    @Override
    public void calculation(RecordList rs, Object oldVal) {
        Set<Record> distinctEntries = new HashSet<Record>(rs.toList());

        super.setValue(distinctEntries.size());
        super.setNumericVal(((Number) distinctEntries.size()).longValue());
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
    public boolean checkConformance(ProfileStatistic<Object> m, double threshold) {        return false;
    }
}
