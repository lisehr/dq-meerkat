package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>SummaryProfileStatistic</h2>
 * <summary>
 * Base class for summarization algorithms using {@link Attribute} elements.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
public abstract class SummaryProfileStatistic extends ProfileStatistic {

    protected SummaryProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        super(title, cat, refProf);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        var attribute = (Attribute) getRefElem();
        List<Number> list = rs.toList().stream()
                .map(record -> (Number) record.getField(attribute.getLabel()))
                .collect(Collectors.toList());
        try {
            calculationNumeric(list, null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, getValue());
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }
}
