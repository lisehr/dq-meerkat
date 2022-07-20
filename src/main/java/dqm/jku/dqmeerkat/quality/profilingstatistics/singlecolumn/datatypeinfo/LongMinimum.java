package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.LongResultProfileStatistic;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.min;

/**
 * <h2>LongMinimum</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.07.2022
 */
public class LongMinimum extends LongResultProfileStatistic<Long> {


    public LongMinimum(DataProfile d) {
        super(min, dti, d, Long.class);
    }

    @Override
    public void calculation(RecordList rs, Long oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        var val = Objects.requireNonNullElse(oldVal, getBasicInstance());
        if (ensureDataTypeCorrect(a.getDataType())) {
            for (Record r : rs) {
                var field = r.getField(a);
                if (field == null) {
                    continue;
                }
                val = getMinimum(val, (long) r.getField(a));
            }
        } else {
            LOGGER.warn("Field {} is not of type Double for {}, skipping it...", a.getLabel(),
                    this.getClass().getSimpleName());
        }
        this.setValue(val);
    }

    /**
     * Creates a basic instance used as a reference (in this case the maximum value)
     *
     * @return the reference value
     */
    private Long getBasicInstance() {
        return Long.MAX_VALUE;
    }

    /**
     * Checks the minimum value of two objects
     *
     * @param current the current minimum value
     * @param toComp  the new value to compare
     * @return the new minimum value
     */
    private Long getMinimum(Long current, Long toComp) {
        if (toComp == null) {
            return current;
        }

        return Long.min(current, toComp);

        // TODO string implementation
//        if (a.getDataType().equals(String.class) && !isNumericList)
//            return Integer.min((int) current, ((String) toComp).length());

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
