package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.LongResultProfileStatistic;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.max;

/**
 * <h2>LongMaximum</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 19.07.2022
 */
public class LongMaximum extends LongResultProfileStatistic<Long> {
    public LongMaximum(DataProfile d) {
        super(max, dti, d, Long.class);
    }

    @Override
    public void calculation(RecordList rs, Long oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        long val = Objects.requireNonNullElse(oldVal, getBasicInstance());
        if (ensureDataTypeCorrect(a.getDataType())) {
            for (Record r : rs) {
                var field = r.getField(a);
                if (field == null) {
                    continue;
                }
                val = getMaximum(val, (long) field);
            }
        } else {
            LOGGER.warn("Attribute {} has wrong data type {} for {}: {}. Skipping calculation.", a.getLabel(), a.getDataType(),
                    getClass().getSimpleName(), inputValueClass.getSimpleName());
        }
        this.setValue(val);
    }

    /**
     * Creates a basic instance used as a reference (in this case the minimum value)
     *
     * @return the reference value
     */
    protected Long getBasicInstance() {
        return Long.MIN_VALUE;
    }

    /**
     * Checks the maximum value of two objects
     *
     * @param current the current maximum value
     * @param toComp  the new value to compare
     * @return the new maximum value
     */
    private Long getMaximum(Long current, Long toComp) {
        if (toComp == null) {
            return current; // if the maximum of the former processed records is null, this record is the maximum
        }
        // TODO move to string implementation
//        if (a.getDataType().equals(String.class) && !isNumericList) {
//            return Integer.max((int) current, ((String) toComp).length());

        return Long.max(current, ((Number) toComp).longValue());


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
