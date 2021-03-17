package dqm.jku.dqmeerkat.quality.profilingmetrics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingmetrics.ProfileMetric;
import dqm.jku.dqmeerkat.quality.profilingmetrics.singlecolumn.datatypeinfo.Maximum;
import dqm.jku.dqmeerkat.util.AttributeSet;

import java.util.List;
import java.util.regex.Pattern;

import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricTitle.*;

public class MaximumEntry extends ProfileMetric {

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public MaximumEntry() {

    }

    public MaximumEntry(DataProfile d) {
        super(maximum, graphCat, d);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        Concept c = (Concept) super.getRefElem();
        Object val = null;

        Attribute a = getAttribute(rs, c);

        boolean isNumeric = checkNumeric(rs, a);
        Class clazz = null;

        if (isNumeric) {
            clazz = getNumericClass(rs, a);

        } else {
            clazz = String.class;
        }

        if(oldVal == null) {
            val = getBasicInstance(clazz);
        } else {
            val = oldVal;
        }
        for(Record r : rs) {
            Object field = r.getField(a);
            val = getMaximum(val, field, isNumeric, clazz);
        }

        this.setValue(val);
        this.setNumericVal(((Number) val).doubleValue());
        this.setValueClass(clazz);
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {

    }

    @Override
    public void update(RecordList rs) {

    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileMetric m, double threshold) {
        return false;
    }

    /**
     * Creates a basic instance used as a reference (in this case the minimum value)
     *
     * @return the reference value
     */
    private Object getBasicInstance(Class clazz) {
        if (clazz.equals(Long.class)) {
            return Long.valueOf(Long.MIN_VALUE);
        }
        else if (clazz.equals(Double.class)) {
            return Double.valueOf(Double.MIN_VALUE);
        }
        else {
            return Integer.MIN_VALUE;
        }
    }

    private Object getMaximum(Object current, Object toComp, boolean isNumeric, Class clazz) {
        if (toComp == null) {
            return current;
        }

        if (clazz.equals(Long.class)) {

            return Long.max((long) current, ((Number) Long.parseLong(toComp.toString())).longValue());
        }
        else if (clazz.equals(Double.class)) {
            return Double.max((double) current, ((Number) Double.parseDouble(toComp.toString())).doubleValue());
        }
        else if (clazz.equals(String.class)) {
            return Integer.max((int) current, ((String) toComp).length());
        }
        else {
            return Integer.max((int) current, ((Number) Integer.parseInt(toComp.toString())).intValue());
        }
    }

    private Attribute getAttribute(RecordList rl, Concept c) {

        AttributeSet as = c.getAttributes();

        Record r = rl.toList().get(0);

        for(Attribute a : as) {
            Object val = r.getField(a);

            if(val != null) {
                return a;
            }
        }
        return null;
    }

    private boolean checkNumeric(RecordList rl, Attribute a) {

        boolean isNumeric = true;

        for(Record r : rl) {
            String field = r.getField(a).toString();

            if(field != null && !pattern.matcher(field).matches()) {
                isNumeric = false;
            }
        }

        return isNumeric;
    }

    private Class getNumericClass(RecordList rl, Attribute a) {

        Class clazz = Integer.class;

        for(Record r : rl) {
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
