package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Representation of the summary. The key is the item and the value is the number of occurrences in the dataset.
     */
    protected Map<Object, Integer> summary = new HashMap<>();

    protected SummaryProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        super(title, cat, refProf);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        rs.toList().stream()
                .map(record -> record.getField(getRefElem().getLabel()))
                .forEach(this::handleCounter);
        setValue(summary);
        setValueClass(summary.getClass());

    }

    protected abstract void handleCounter(Object value);

    @Override
    public void update(RecordList rs) {
        calculation(rs, getValue());
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }
}
