package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.NumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.AttributeSet;

import java.util.regex.Pattern;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.graphCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.maximum;

public class MaximumEntry extends NumberProfileStatistic<Double> {

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public MaximumEntry(DataProfile d) {
        super(maximum, graphCat, d, Double.class);
    }

    @Override
    public void calculation(RecordList rs, Double oldVal) {
        Concept c = (Concept) super.getRefElem();
        double val;

        Attribute a = getAttribute(rs, c);

        if (oldVal == null) {
            val = getBasicInstance();
        } else {
            val = oldVal;
        }
        for (Record r : rs) {
            var field = (double) r.getField(a);
            val = getMaximum(val, field);
        }

        this.setValue(val);
        this.setValueClass(Double.class);
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, value);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Double> m, double threshold) {
        return false;
    }

    /**
     * Creates a basic instance used as a reference (in this case the minimum value)
     *
     * @return the reference value
     */
    private Double getBasicInstance() {
        return Double.MIN_VALUE;
    }

    private double getMaximum(double current, double toComp) {
        return Double.max(current, toComp);
    }

    private Attribute getAttribute(RecordList rl, Concept c) {

        AttributeSet as = c.getAttributes();

        Record r = rl.toList().get(0);

        for (Attribute a : as) {
            Object val = r.getField(a);

            if (val != null) {
                return a;
            }
        }
        return null;
    }

    private boolean checkNumeric(RecordList rl, Attribute a) {

        boolean isNumeric = true;

        for (Record r : rl) {
            String field = r.getField(a).toString();

            if (field != null && !pattern.matcher(field).matches()) {
                isNumeric = false;
            }
        }

        return isNumeric;
    }

    private Class getNumericClass(RecordList rl, Attribute a) {

        Class clazz = Integer.class;

        for (Record r : rl) {
            String field = r.getField(a).toString();

            try {
                int num = Integer.parseInt(field);
            } catch (NumberFormatException e) {
                try {
                    double num = Double.parseDouble(field);
                    clazz = Double.class;
                } catch (NumberFormatException ex) {

                }
            }
        }
        return clazz;
    }
}
