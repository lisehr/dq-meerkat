package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;

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
        var tmpMap = new HashMap<Object, Integer>();
        for (Record record : rs) {
            var value = record.getField(attribute.getLabel());
            if (mkCounter.containsKey(value)) {
                mkCounter.put(value, mkCounter.get(value) + 1);
            } else {
                mkCounter.put(value, 1);
            }
            if (mkCounter.size() >= k) {
                mkCounter = mkCounter.entrySet().stream()
                        .map(objectIntegerEntry -> objectIntegerEntry.setValue(objectIntegerEntry.getValue() - 1))
                        .collect(Collectors.toMap(o -> o, integer -> integer));

                mkCounter.forEach((o, integer) -> {
                    if (integer > 0) {
                        tmpMap.put(o, integer);
                    }
                });
                mkCounter = tmpMap;
            }
        }
        setValue(mkCounter);
        setValueClass(mkCounter.getClass());
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {

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
