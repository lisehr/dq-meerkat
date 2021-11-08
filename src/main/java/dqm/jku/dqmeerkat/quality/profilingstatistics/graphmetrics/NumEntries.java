package dqm.jku.dqmeerkat.quality.profilingmetrics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricTitle.*;

import java.util.List;

public class NumEntries extends ProfileMetric {

    public NumEntries() {

    }

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
    public boolean checkConformance(ProfileMetric m, double threshold) {
        // Never evaluates to false, because the reference is here the size of the RDP and should not be compared to the batch size of the DPs
        return true;
    }
}
