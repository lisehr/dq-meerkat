package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.util.Constants;
import org.jetbrains.annotations.NotNull;

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
        super(StatisticTitle.hist, StatisticCategory.histCat, refProf);
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

    private static double calculateConformance(@NotNull Map<Object, Integer> summary) {
        return summary.size() * .1D + summary.entrySet()
                .stream()
                .mapToDouble(value -> ((int) value.getKey()) * value.getValue())
                .average()
                .orElse(0);
    }

    @Override
    public boolean checkConformance(ProfileStatistic m, double threshold) {
        var rdpVal = calculateConformance(summary);
        var dpValue = calculateConformance((Map<Object, Integer>) m.getValue());


        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG)
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        return conf;
    }
}