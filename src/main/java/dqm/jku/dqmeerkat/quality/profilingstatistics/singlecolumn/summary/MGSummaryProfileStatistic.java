package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h2>MKSummaryProfileStatistic</h2>
 * <summary>
 * {@link AbstractProfileStatistic} implementation for the Misra- and Gries summarization algorithm. It counts the number of
 * records and, depending on the summary size k, removes the least frequent ones from the summary.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 04.07.2022
 */
public class MGSummaryProfileStatistic extends SummaryProfileStatistic<Double> {


    /**
     * parameter defining the maximum possible size of the summary. If the size of the summary exceeds this value, all
     * counters in the summary are decremented and any, that are below 1 are removed.
     */
    private final int k;

    public MGSummaryProfileStatistic(DataProfile refProf, int k) {
        super(StatisticTitle.summary, StatisticCategory.summaryCategory, refProf);
        this.k = k;
    }


    /**
     * Handles the counter for the given value. The counter is handled according to the MG Summary algorithm,
     * which tries to maintain a summary of size k. Each new element is added to the summary with a counter of 1
     * if the size k has not been reached. Otherwise, each counter is decremented and any, that are below 1 are removed.
     *
     * @param value the value to handle, i.E. either add it to the summary, increment the counter of the value or
     *              compress the summary.
     */
    @Override
    protected void handleCounter(Double value) {
        // if the item is in the summary, increment it
        if (summary.containsKey(value)) {
            summary.put(value, summary.get(value) + 1);
        } else { // otherwise check if the summary is full and if so reduce all counters by one, remove the items with counters lower than 1
            if (summary.size() >= k) {
                var tmpMap = new HashMap<Double, Integer>();
                summary = summary.entrySet().stream()
                        .peek(objectIntegerEntry -> objectIntegerEntry.setValue(objectIntegerEntry.getValue() - 1))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                // "copy" map and remove anything that is 0
                summary.forEach((o, integer) -> {
                    if (integer > 0) {
                        tmpMap.put(o, integer);
                    }
                });
                summary = tmpMap;
            } else { // if the summary is not full, add the item to the summary
                summary.put(value, 1);
            }
        }
    }

    @Override
    public double calculateConformance() {
        // TODO fixup after generic implementation is done
        var avgCounters = summary.values().stream().mapToInt(i -> i).average().orElse(0);
        return (double) summary.size() / k + avgCounters / k;
    }
}
