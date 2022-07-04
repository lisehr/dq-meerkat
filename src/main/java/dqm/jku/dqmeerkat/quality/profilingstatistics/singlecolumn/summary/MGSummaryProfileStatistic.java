package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h2>MKSummaryProfileStatistic</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 04.07.2022
 */
public class MGSummaryProfileStatistic extends ProfileStatistic {

    /**
     * TODO
     */
    private Map<Object, Integer> mkCounter = new HashMap<>();

    /**
     * TODO
     */
    private final int k;

    public MGSummaryProfileStatistic(DataProfile refProf, int k) {
        super(StatisticTitle.hist, StatisticCategory.histCat, refProf);
        this.k = k;
    }


    @Override
    public void calculation(RecordList rs, Object oldVal) {
        var attribute = (Attribute) getRefElem();
        List<Number> list = rs.toList().stream().map(record -> (Number) record.getField(attribute.getLabel())).collect(Collectors.toList());
        try {
            calculationNumeric(list, null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
        for (Number value : list) {
            if (value instanceof Double) {
                var symbols = DecimalFormatSymbols.getInstance();
                symbols.setDecimalSeparator('.');
                DecimalFormat df = new DecimalFormat("##.####", symbols);
                value = Double.parseDouble(df.format(value));
            }


            if (mkCounter.containsKey(value)) {
                mkCounter.put(value, mkCounter.get(value) + 1);
            } else {
                if (mkCounter.size() >= k) {
                    var tmpMap = new HashMap<Object, Integer>();
                    mkCounter = mkCounter.entrySet().stream()
                            .peek(objectIntegerEntry -> objectIntegerEntry.setValue(objectIntegerEntry.getValue() - 1))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    mkCounter.forEach((o, integer) -> {
                        if (integer > 0) {
                            tmpMap.put(o, integer);
                        }
                    });
                    mkCounter = tmpMap;
                } else {
                    mkCounter.put(value, 1);
                }

            }

        }
        setValue(mkCounter);
        setValueClass(mkCounter.getClass());
    }


    @Override
    public void update(RecordList rs) {

    }

    @Override
    protected String getValueString() {
        return null;
    }

    @Override
    public boolean checkConformance(ProfileStatistic m, double threshold) {
        return false;
    }
}
