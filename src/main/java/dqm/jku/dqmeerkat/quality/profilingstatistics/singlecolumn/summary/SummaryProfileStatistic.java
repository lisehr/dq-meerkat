package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.util.Constants;

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
public abstract class SummaryProfileStatistic<T> extends ProfileStatistic<Map<T, Integer>> {

    /**
     * Representation of the summary. The key is the item and the value is the number of occurrences in the dataset.
     */
    protected Map<T, Integer> summary = new HashMap<>();

    protected SummaryProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        super(title, cat, refProf);
    }

    @Override
    public void calculation(RecordList rs, Map<T, Integer> oldVal) {
        rs.toList().stream()
                .map(record -> (T)record.getField(getRefElem().getLabel()))
                .forEach(this::handleCounter);
        setValue(summary);
        setValueClass(summary.getClass());

    }

    /**
     * Handles the counter for the given value. Should be called by the calculation methods. The actual handling of
     * the counter is done by the concrete implementations.
     *
     * @param value the value to handle, i.E. either add it to the summary, increment the counter of the value or
     *              compress the summary.
     */
    protected abstract void handleCounter(T value);

    /**
     * Calculates a value used to determine conformance of this {@link AbstractProfileStatistic} to another
     *
     * @return the conformance value as double
     */
    public abstract double calculateConformance();

    @Override
    public boolean checkConformance(ProfileStatistic<Map<T, Integer>> m, double threshold) {
        var rdpVal = calculateConformance();
        var dpValue = ((SummaryProfileStatistic<T>) m).calculateConformance();


        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG) {
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        }
        return conf;
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
