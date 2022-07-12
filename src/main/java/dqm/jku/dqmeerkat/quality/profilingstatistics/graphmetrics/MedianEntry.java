package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.AttributeSet;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;

public class MedianEntry extends AbstractProfileStatistic {

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");


    public MedianEntry(DataProfile d) {
        super(median, graphCat, d);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        Concept c = (Concept) super.getRefElem();

        Attribute a = getAttribute(rs, c);

        boolean isNumeric = checkNumeric(rs, a);
        Class clazz = null;

        if (isNumeric) {
            clazz = getNumericClass(rs, a);

        } else {
            clazz = String.class;
        }

        List<Number> list = new ArrayList<Number>();

        for (Record r : rs) {
            Number field = null;
            if (clazz.equals(String.class) && r.getField(a) != null) {
                field = ((String) r.getField(a)).length();
            }
            else {

                if(clazz.equals(Integer.class)) {
                    int num = Integer.parseInt(r.getField(a).toString());
                    field = (Number) num;
                } else if(clazz.equals(Long.class)) {
                    long num = Long.parseLong(r.getField(a).toString());
                    field = (Number) num;
                }
            }

            if (field != null) {
                list.add(field);
            }
        }

        list.sort(new NumberComparator());
        Object val = getMedian(list, rs.size(), clazz);
        this.setValue(val);
        this.setNumericVal(((Number) val).doubleValue());
        this.setValueClass(a.getDataType());
    }

    private Object getMedian(List<Number> list, int size, Class clazz) {
        boolean isEven = false;
        if (list.size() < size) {
            return null;
        }

        if (size % 2 == 0) {
            isEven = true;
        }
        size /= 2;
        Number val = list.get(size);
        if (isEven) {
            if (size == 1) {
                val = averageResult(val, list.get(0), clazz);
            } else {
                val = averageResult(val, list.get(size + 1), clazz);
            }
        }
        //if (((Attribute) super.getRefElem()).getDataType().equals(Long.class)) {
        if (clazz.equals(Long.class)) {
            return val.longValue();
        }
        else if (clazz.equals(Double.class)) {
            return val.doubleValue();
        }
        return val;
    }

    private Number averageResult(Number oddMedian, Number next, Class clazz) {
        //Attribute a = (Attribute) super.getRefElem();
        if (clazz.equals(Long.class)) {
            return ((oddMedian.longValue() + oddMedian.longValue()) / 2);
        }
        else if (clazz.equals(Double.class)) {
            return ((oddMedian.doubleValue() + oddMedian.doubleValue()) / 2);
        }
        return oddMedian;
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
    public boolean checkConformance(ProfileStatistic<Object> m, double threshold) {        return false;
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
