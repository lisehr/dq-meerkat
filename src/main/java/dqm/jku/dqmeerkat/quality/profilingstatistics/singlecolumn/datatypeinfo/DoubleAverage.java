package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentNumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.avg;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numrows;


/**
 * Describes the metric Average, where the average of all values in an Attribute
 * is taken.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Average")
public class DoubleAverage extends DependentNumberProfileStatistic<Double, Double> {


    public DoubleAverage(DataProfile d) {
        super(avg, dti, d, Double.class);
    }

    @Override
    public void calculation(RecordList rs, Double oldVal) {
        this.dependencyCalculationWithRecordList(rs);
        double val = Objects.requireNonNullElse(oldVal, getBasicInstance());
        if (ensureDataTypeCorrect(((Attribute) (getRefElem())).getDataType())) {
            for (Record r : rs) {
                var castedDouble = (Double) r.getField((Attribute) super.getRefElem());
                if (castedDouble != null) {
                    val += castedDouble;
                }
            }
        }
        val = performAveraging(val);
        this.setValue(val);
        this.setInputValueClass(Double.class);
        this.setOutputValueClass(Double.class);
    }

    /**
     * Method for getting the average value of the objects
     *
     * @param sum the sum of values
     * @return the average value
     */
    private Double performAveraging(Double sum) {
        // TODO deduct type of value from statistics
        long numRows = (long) super.getRefProf().getStatistic(numrows).getValue();
        int scale = 3; // Nachkommastellen für Dezimal Division
        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(numRows), scale, RoundingMode.HALF_UP).doubleValue(); // ab 0.5 aufrunden!
    }

    /**
     * Creates a basic instance used as a reference (in this case zero as a number)
     *
     * @return the reference value
     */
    protected Double getBasicInstance() {
        return 0D;
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
    private Double getOriginalSum() {
        return ((Number) super.getValue()).doubleValue() * (long) super.getRefProf().getStatistic(numrows).getValue();
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
