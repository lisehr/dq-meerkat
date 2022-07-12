package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentNumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.ArrayList;
import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/MedianAbsoluteDeviation")
public class MedianAbsoluteDeviation extends DependentNumberProfileStatistic<Double> {

    public MedianAbsoluteDeviation(DataProfile d) {
        super(mad, dti, d);
    }

    private void calculation(RecordList rl, Double oldVal, boolean checked) {
        if (!checked)
            this.dependencyCalculationWithRecordList(rl);

        double medVal = (double) super.getRefProf().getStatistic(med).getValue();
        List<Number> medians = new ArrayList<>();
        for (Record r : rl) {
            double field = (double) r.getField((Attribute) super.getRefElem());
            medians.add(field - medVal);
        }


        Median medM = new Median(this.getRefProf());
        // TODO implement list to recordlist conversion
        medM.calculation(new RecordList(medians, "dummy"), null);
        var med = medM.getValue();
        this.setValue(med);
        this.setValueClass(Double.class);
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
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(med))
            super.getRefProf().getStatistic(med).calculation(rl, null);
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
            medM = new Median(super.getRefProf());
            super.getRefProf().addStatistic(medM);
        }

    }

    @Override
    protected Double getBasicInstance() {
        return Double.MIN_VALUE;
    }
}
