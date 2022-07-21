package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentDoubleResultProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.ArrayList;
import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/MedianAbsoluteDeviation")
public class DoubleMedianAbsoluteDeviation extends DependentDoubleResultProfileStatistic<Double> {

    public DoubleMedianAbsoluteDeviation(DataProfile d) {
        super(mad, dti, d, Double.class);
    }

    private void calculation(RecordList rl, Double oldVal, boolean checked) {
        if (!checked) {
            this.dependencyCalculationWithRecordList(rl);
        }

        double medVal = super.getRefProf().getStatistic(avg).getValue() == null ?
                0D : (double) super.getRefProf().getStatistic(avg).getValue();
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
        medM.calculation(new RecordList(medians, "dummy"), null);
        var med = medM.getValue();
        this.setValue(med);
        this.setInputValueClass(Double.class);
    }

    @Override
    public void calculation(RecordList rl, Double oldVal) {
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
    protected Double getBasicInstance() {
        return Double.MIN_VALUE;
    }
}
