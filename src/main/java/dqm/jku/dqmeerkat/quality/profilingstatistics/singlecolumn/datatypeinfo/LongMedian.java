package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.LongResultProfileStatistic;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;

import java.util.ArrayList;
import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.med;

/**
 * <h2>LongMedian</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.07.2022
 */
public class LongMedian extends LongResultProfileStatistic<Long> {

    public LongMedian(DataProfile d) {
        super(med, dti, d, Long.class);
    }

    @Override
    public void calculation(RecordList rs, Long oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        List<Number> list = new ArrayList<Number>();
        if (ensureDataTypeCorrect(a.getDataType())) {
            for (Record r : rs) {
                Number field;
                // TODO implement for Strings
//            if (a.getDataType().equals(String.class) && r.getField(a) != null)
//                field = ((String) r.getField(a)).length();
                field = (Number) r.getField(a);
                if (field != null) {
                    list.add(field);
                }
            }
        }
        list.sort(new NumberComparator());
        var val = getMedian(list, list.size());
        this.setValue(val);
    }

    /**
     * Method for getting the median of a list of data
     *
     * @param list the list of data (sorted in ascending order)
     * @param size the size of records
     * @return the median of the list
     */
    private long getMedian(List<Number> list, int size) {
        boolean isEven = false;
        if (list.size() < size || list.isEmpty()) {
            return Long.MIN_VALUE;
        }
        if (size % 2 == 0) {
            isEven = true;
        }
        size /= 2;
        Number val = list.get(size);
        if (isEven) {
            if (size == 1) {
                val = averageResult(val, list.get(0));
            } else {
                val = averageResult(val, list.get(size + 1));
            }
        }
        return val.longValue();
    }

    /**
     * Method for averaging the result, used if the number of measured records is
     * even
     *
     * @param oddMedian the median value if the amount of records is odd
     * @param next      the follow up value
     * @return the weighted median
     */
    private Number averageResult(Number oddMedian, Number next) {
        return ((oddMedian.doubleValue() + next.doubleValue()) / 2);
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, super.getValue());
    }


    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }
}
