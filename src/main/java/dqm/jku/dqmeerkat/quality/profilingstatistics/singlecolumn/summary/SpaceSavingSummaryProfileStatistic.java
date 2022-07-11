package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.util.Constants;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Map;

/**
 * <h2>SpaceSavingSummaryProfileStatistic</h2>
 * <summary>
 * {@link ProfileStatistic} implementation of the Space Saving Algorithm. It generates a summary of the given
 * data by counting the occurrences of samples in the data. The summary has a maximum size of k, whenever it reaches this
 * size it is compressed again.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
public class SpaceSavingSummaryProfileStatistic extends SummaryProfileStatistic {


    /**
     * the fixed size of the summary. when ths size is reached, the least frequent items are replaced with more
     * current items from the dataset.
     */
    private final int k;

    public SpaceSavingSummaryProfileStatistic(DataProfile refProf, int k) {
        super(StatisticTitle.summary, StatisticCategory.summaryCategory, refProf);
        this.k = k;
    }


    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
        for (Number value : list) {
            // round doubles to 4 decimals
            if (value instanceof Double) {
                var symbols = DecimalFormatSymbols.getInstance();
                symbols.setDecimalSeparator('.');
                DecimalFormat df = new DecimalFormat("##.####", symbols);
                value = Double.parseDouble(df.format(value));
            }
            handleCounter(value);
        }
        setValue(summary);
        setValueClass(summary.getClass());
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
    protected void handleCounter(Object value) {
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
    public Class<?> getValueClass() {
        return summary.getClass();
    }

    public double calculateConformance() {
        // TODO fixup when using generics -> use key values as well
        var avgCounters = summary.values().stream().mapToInt(i -> i).average().orElse(0);
        return (double) summary.size() / k + avgCounters / k;
    }

    @Override
    public boolean checkConformance(ProfileStatistic m, double threshold) {
        var rdpVal = calculateConformance();
        var dpValue = ((SummaryProfileStatistic) m).calculateConformance();


        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG)
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        return conf;
    }
}
