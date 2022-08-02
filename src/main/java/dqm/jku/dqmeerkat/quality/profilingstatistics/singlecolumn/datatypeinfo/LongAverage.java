package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentDoubleResultProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.avg;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numrows;

/**
 * <h2>LongAverage</h2>
 * <summary>Average statistic implementation for Long data types. </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 19.07.2022
 */
public class LongAverage extends DependentDoubleResultProfileStatistic<Long> {
    public LongAverage(DataProfile refProf) {
        super(avg, StatisticCategory.dti, refProf, Long.class);
    }

    @Override
    public void calculation(RecordList rs, Long oldVal) {
        this.dependencyCalculationWithRecordList(rs);
        long val = Objects.requireNonNullElse(oldVal, getBasicInstance());
        if (ensureDataTypeCorrect(((Attribute) (getRefElem())).getDataType())) {
            for (Record r : rs) {
                var castedLong = (Long) r.getField((Attribute) super.getRefElem());
                if (castedLong != null) {
                    val += castedLong;
                }
            }
        }

        this.setValue(performAveraging(val));
        this.setOutputValueClass(Double.class);
        this.setInputValueClass(Long.class);
    }

    /**
     * Method for getting the average value of the objects
     *
     * @param sum the sum of values
     * @return the average value
     */
    private Double performAveraging(Long sum) {
        // TODO deduct type of value from statistics
        long numRows = (long) super.getRefProf().getStatistic(numrows).getValue();
        return ((double) sum / numRows);
    }

    /**
     * Creates a basic instance used as a reference (in this case zero as a number)
     *
     * @return the reference value
     */
    protected Long getBasicInstance() {
        return 0L;
    }


    @Override
    public void update(RecordList rs) {
        calculation(rs, getOriginalSum());
    }

    /**
     * Restores the original sum of the averaging value
     *
     * @return the sum before weighting with the amount of values
     */
    private Long getOriginalSum() {
        return ((Number) super.getValue()).longValue() * (long) super.getRefProf().getStatistic(numrows).getValue();
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(avg) - 2 <= super.getMetricPos(numrows)) {
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
        }
    }

    @Override
    protected void dependencyCheck() {
        var sizeM = super.getRefProf().getStatistic(numrows);
        if (sizeM == null) {
            sizeM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(sizeM);
        }
    }
}
