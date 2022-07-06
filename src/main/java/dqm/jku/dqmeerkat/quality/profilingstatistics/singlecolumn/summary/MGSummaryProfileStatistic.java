package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.util.Constants;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h2>MKSummaryProfileStatistic</h2>
 * <summary>
 * {@link ProfileStatistic} implementation for the Misra- and Gries summarization algorithm. It counts the number of
 * records and, depending on the summary size k, removes the least frequent ones from the summary.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 04.07.2022
 */
public class MGSummaryProfileStatistic extends SummaryProfileStatistic {

    /**
     * Representation of the summary. The key is the item and the value is the number of occurrences in the dataset.
     */
    private Map<Object, Integer> mkCounter = new HashMap<>();

    /**
     * parameter defining the maximum possible size of the summary. If the size of the summary exceeds this value, all
     * counters in the summary are decremented and any, that are below 1 are removed.
     */
    private final int k;

    public MGSummaryProfileStatistic(DataProfile refProf, int k) {
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

            // if the item is in the summary, increment it
            if (mkCounter.containsKey(value)) {
                mkCounter.put(value, mkCounter.get(value) + 1);
            } else { // otherwise check if the summary is full and if so reduce all counters by one, remove the items with counters lower than 1
                if (mkCounter.size() >= k) {
                    var tmpMap = new HashMap<Object, Integer>();
                    mkCounter = mkCounter.entrySet().stream()
                            .peek(objectIntegerEntry -> objectIntegerEntry.setValue(objectIntegerEntry.getValue() - 1))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    // "copy" map and remove anything that is 0
                    mkCounter.forEach((o, integer) -> {
                        if (integer > 0) {
                            tmpMap.put(o, integer);
                        }
                    });
                    mkCounter = tmpMap;
                } else { // if the summary is not full, add the item to the summary
                    mkCounter.put(value, 1);
                }
            }
        }
        setValue(mkCounter);
        setValueClass(mkCounter.getClass());
    }

    @Override
    public boolean checkConformance(ProfileStatistic m, double threshold) {
        var rdpAvg = mkCounter.values().stream().mapToInt(i -> i).average().orElse(0);
        var rdpVal = mkCounter.size() * 0.1 + rdpAvg;

        var dpMap = ((Map<Object, Integer>) m.getValue());
        var dpAvg = dpMap.values().stream().mapToInt(i -> i).average().orElse(0);
        var dpValue = dpMap.size() * 0.1 + dpAvg;


        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG)
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        return conf;
    }
}
