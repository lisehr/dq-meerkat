package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;

import java.util.Map;

import static dqm.jku.dqmeerkat.util.GenericsUtil.cast;

/**
 * <h2>SpaceSavingSummaryProfileStatistic</h2>
 * <summary>
 * {@link AbstractProfileStatistic} implementation of the Space Saving Algorithm. It generates a summary of the given
 * data by counting the occurrences of samples in the data. The summary has a maximum size of k, whenever it reaches this
 * size it is compressed again.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
public class SpaceSavingSummaryProfileStatistic extends SummaryProfileStatistic<Double> {


    /**
     * the fixed size of the summary. when ths size is reached, the least frequent items are replaced with more
     * current items from the dataset.
     */
    private final int k;

    public SpaceSavingSummaryProfileStatistic(DataProfile refProf, int k) {
        super(StatisticTitle.summary, StatisticCategory.summaryCategory, refProf);
        this.k = k;
    }

    /**
     * Handles the summary by applying the space saving algorithm. The algorithm is as follows:
     * If the current value is not in the summary, it is added to the summary if the size of the summary is below k.
     * If the size of the summary is above k, the least frequent item is removed from the summary and the current item is
     * added in place of said item.
     * If the item is already in the summary, the counter is incremented.
     *
     * @param value the value to handle, i.E. either add it to the summary, increment the counter of the value or
     *              compress the summary.
     */
    @Override
    protected void handleCounter(Double value) {
        // if the item is in the summary, increment it
        if (summary.containsKey(value)) {
            summary.put(value, summary.get(value) + 1);
        } else { // otherwise check if the summary is full and if so replace the item with the lowest counter with the current item
            if (summary.size() >= k) {
                var entryToRemove = summary.entrySet()
                        .stream()
                        .min(Map.Entry.comparingByValue())
                        .orElseThrow();
                summary.remove(entryToRemove.getKey());
                summary.put(value, 1);

            } else {
                summary.put(value, 1);
            }
        }
    }

    @Override
    public Class<Map<Double, Integer>> getInputValueClass() {
        return cast(summary.getClass());
    }

    public double calculateConformance() {
        // TODO fixup when using generics -> use key values as well
        var avgCounters = summary.values().stream().mapToInt(i -> i).average().orElse(0);
        return (double) summary.size() / k + avgCounters / k;
    }

}
