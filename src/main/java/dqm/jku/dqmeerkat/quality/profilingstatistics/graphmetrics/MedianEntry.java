package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DoubleResultProfileStatistic;
import dqm.jku.dqmeerkat.util.AttributeSet;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.graphCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.median;

public class MedianEntry extends DoubleResultProfileStatistic<Double> {

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");


    public MedianEntry(DataProfile d) {
        super(median, graphCat, d, Double.class);
    }

    @Override
    public void calculation(RecordList rs, Double oldVal) {
        Concept c = (Concept) super.getRefElem();

        Attribute a = getAttribute(rs, c);

        // TODO string implementation
        List<Number> list = rs.toList().stream()
                .map(record -> (double) record.getField(a))
                .sorted(new NumberComparator())
                .collect(Collectors.toList());


        double val = getMedian(list, rs.size());
        this.setValue(val);
    }

    private double getMedian(List<Number> list, int size) {
        boolean isEven = false;
        if (list.size() < size) {
            return -1;
        }

        if (size % 2 == 0) {
            isEven = true;
        }
        size /= 2;
        Number val = list.get(size);
        if (isEven) {
            if (size == 1) {
                val = averageResult(val, list.get(0));
            } else {
                val = averageResult(val, list.get(size + 1));
            }
        }

        return val.doubleValue();
    }

    private Number averageResult(Number oddMedian, Number next) {
        return ((oddMedian.doubleValue() + next.doubleValue()) / 2);
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, value);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
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
