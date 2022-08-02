package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentDoubleResultProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

/**
 * This class computes the standard deviation of a sample. It does not compute the standard deviation of a population.
 */

@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/StandardDeviation")
public class DoubleStandardDeviation extends DependentDoubleResultProfileStatistic<Double> {
    public DoubleStandardDeviation(DataProfile d) {
        super(sd, dti, d, Double.class);
    }

    private void calculation(RecordList rl, Double oldVal, boolean checked) {
        if (!checked) {
            this.dependencyCalculationWithRecordList(rl);
        }
        var avgVal = super.getRefProf().getStatistic(avg).getValue() == null ?
                0D : (double) super.getRefProf().getStatistic(avg).getValue();
        var val = Objects.requireNonNullElse(oldVal, 0D);
        ;
        if (ensureDataTypeCorrect(((Attribute) super.getRefElem()).getDataType())) {
            for (Record r : rl) {
                var field = (r.getField((Attribute) super.getRefElem()));
                if (field == null) {
                    continue;
                }
                val = addValue(val, (double) field, avgVal);
            }
            val = performAveraging(val);
        } else {
            LOGGER.warn("Field {} is not of type Double for {}, skipping it...", getRefElem().getLabel(),
                    this.getClass().getSimpleName());
            return;
        }
        this.setValue(val);
        this.setInputValueClass(Double.class);
    }

    /**
     * Adds a value to the sum of squared differences
     *
     * @param current the current sum of values
     * @param toAdd   the value to be added
     * @param avg     the average value to substract
     * @return the new sum of values
     */
    private Double addValue(Double current, Double toAdd, Double avg) {
        if (toAdd == null) {
            return current;
        }
//        if (a.getClass().equals(String.class)) // TODO move to string implementation
//            return (int) current + (int) Math.pow(((String) toAdd).length() - ((String) avg).length(), 2);
        return current + Math.pow((((Number) toAdd).doubleValue() - ((Number) avg).doubleValue()), 2);
    }

    /**
     * Method for getting the square root of the average value of the objects
     *
     * @param sum the sum of values
     * @return the square root of the average value
     */
    private Double performAveraging(Double sum) {
        if (((long) super.getRefProf().getStatistic(numrows).getValue()) == 1) {
            return sum;
        }
        return Math.sqrt((sum / ((long) super.getRefProf().getStatistic(numrows).getValue() - 1)));
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
        if (super.getMetricPos(sd) - 1 <= super.getMetricPos(avg)) {
            super.getRefProf().getStatistic(avg).calculation(rl, null);
        }
    }

    @Override
    protected void dependencyCheck() {
        var numrowM = super.getRefProf().getStatistic(numrows);
        if (numrowM == null) {
            numrowM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(numrowM);
        }
        var avgM = super.getRefProf().getStatistic(avg);
        if (avgM == null) {
            avgM = new DoubleAverage(super.getRefProf());
            super.getRefProf().addStatistic(avgM);
        }

    }

    /**
     * Creates a basic instance used as a reference (in this case zero as a number)
     *
     * @return the reference value
     */
    protected Double getBasicInstance() {
        return 0D;
    }

}
