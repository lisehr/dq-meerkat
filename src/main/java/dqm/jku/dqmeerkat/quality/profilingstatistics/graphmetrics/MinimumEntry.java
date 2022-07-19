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
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.minimum;

public class MinimumEntry extends NumberProfileStatistic<Double,Double> {

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");


    public MinimumEntry(DataProfile d) {
        super(minimum, graphCat, d, Double.class);
    }

    @Override
    public void calculation(RecordList rs, Double oldVal) {
        Concept c = (Concept) super.getRefElem();
        Attribute a = getAttribute(rs, c);

        double val = rs.toList().stream()
                .mapToDouble(value1 -> (double) value1.getField(a)).min()
                .orElseThrow();

        this.setValue(val);
        this.setInputValueClass(Double.class);
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
    public boolean checkConformance(ProfileStatistic<Double, Double> m, double threshold) {
        return false;
    }

    private Double getBasicInstance() {
        return Double.MAX_VALUE;
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
