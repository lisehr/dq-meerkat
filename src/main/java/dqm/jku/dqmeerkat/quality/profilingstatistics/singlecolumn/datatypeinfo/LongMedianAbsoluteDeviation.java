package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentNumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;

import java.util.ArrayList;
import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.med;

/**
 * <h2>LongMedianAbsoluteDeviation</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.07.2022
 */
public class LongMedianAbsoluteDeviation  extends DependentNumberProfileStatistic<Long, Double> {

    public LongMedianAbsoluteDeviation(DataProfile d) {
        super(mad, dti, d, Long.class);
    }

    private void calculation(RecordList rl, Long oldVal, boolean checked) {
        if (!checked) {
            this.dependencyCalculationWithRecordList(rl);
        }

        double medVal = (double) super.getRefProf().getStatistic(med).getValue();
        List<Number> medians = new ArrayList<>();
        if (ensureDataTypeCorrect(((Attribute) super.getRefElem()).getDataType())) {
            for (Record r : rl) {
                var field = r.getField((Attribute) super.getRefElem());
                if (field == null) {
                    continue;
                }
                medians.add((double) field - medVal);
            }
        }

        DoubleMedian medM = new DoubleMedian(this.getRefProf());
        // TODO implement list to recordlist conversion
        medM.calculation(new RecordList(medians, "dummy"), null);
        var med = medM.getValue();
        this.setValue(med);
        this.setInputValueClass(Long.class);
    }

    @Override
    public void calculation(RecordList rl, Long oldVal) {
        calculation(rl, oldVal, false);
    }


    @Override
    public void update(RecordList rl) {
        calculation(rl, null, true);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }


    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows)) {
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
        }
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(med)) {
            super.getRefProf().getStatistic(med).calculation(rl, null);
        }
    }

    @Override
    protected void dependencyCheck() {
        var numrowM = super.getRefProf().getStatistic(numrows);
        if (numrowM == null) {
            numrowM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(numrowM);
        }
        var medM = super.getRefProf().getStatistic(med);
        if (medM == null) {
            medM = new DoubleMedian(super.getRefProf());
            super.getRefProf().addStatistic(medM);
        }

    }

    @Override
    protected Long getBasicInstance() {
        return Long.MIN_VALUE;
    }
}