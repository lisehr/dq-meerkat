package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentNumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.util.Constants;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

/**
 * <h2>LongStandardDeviation</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.07.2022
 */
public class LongStandardDeviation extends DependentNumberProfileStatistic<Long, Double> {
    public LongStandardDeviation(DataProfile d) {
        super(sd, dti, d, Long.class);
    }

    private void calculation(RecordList rl, Long oldVal, boolean checked) {
        if (!checked) {
            this.dependencyCalculationWithRecordList(rl);
        }
        var avgVal = (double) super.getRefProf().getStatistic(avg).getValue();
        var val = 0L;
        double ret = 0D;
        val = Objects.requireNonNullElse(oldVal, 0L);
        if (ensureDataTypeCorrect(((Attribute) super.getRefElem()).getDataType())) {
            for (Record r : rl) {
                var field = (r.getField((Attribute) super.getRefElem()));
                if (field == null) {
                    continue;
                }
                val = addValue(val, (long) field, avgVal);
            }
            ret = performAveraging(val);
        }
        this.setValue(ret);
        this.setInputValueClass(Long.class);
    }

    /**
     * Adds a value to the sum of squared differences
     *
     * @param current the current sum of values
     * @param toAdd   the value to be added
     * @param avg     the average value to substract
     * @return the new sum of values
     */
    private Long addValue(Long current, Long toAdd, Double avg) {
        if (toAdd == null) {
            return current;
        }
//        if (a.getClass().equals(String.class)) // TODO move to string implementation
//            return (int) current + (int) Math.pow(((String) toAdd).length() - ((String) avg).length(), 2);
        return (long) (current + Math.pow(toAdd - avg, 2));
    }

    /**
     * Method for getting the square root of the average value of the objects
     *
     * @param sum the sum of values
     * @return the square root of the average value
     */
    private Double performAveraging(Long sum) {
        if (((long) super.getRefProf().getStatistic(numrows).getValue()) == 1) {
            return (double) sum;
        }
        return Math.sqrt(((double) sum / ((long) super.getRefProf().getStatistic(numrows).getValue() - 1)));
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
            avgM = new LongAverage(super.getRefProf());
            super.getRefProf().addStatistic(avgM);
        }

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
    public boolean checkConformance(ProfileStatistic<Long, Double> m, double threshold) {
        if (getValue() == null) {
            setValue(m.getValue());
        }
        double rdpVal = ((Number) this.getValue()).doubleValue();
        double dpValue = ((Number) m.getValue()).doubleValue();

        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG) {
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        }
        return conf;
    }
}
