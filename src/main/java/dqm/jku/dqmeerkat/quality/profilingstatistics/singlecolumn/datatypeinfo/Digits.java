package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.NumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.Constants;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.dig;


/**
 * Describes the metric Digits, which is the amount of digits before the decimal
 * point.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Digits")
public class Digits extends NumberProfileStatistic<Long> {
    public Digits(DataProfile d) {
        super(dig, dti, d, Long.class);
    }

    private int calculateDigits(Object field) {
        String valueStr = String.valueOf(field);
        int value = valueStr.indexOf(".");
        if (value == -1)
            value = valueStr.length();
        if (valueStr.indexOf('-') != -1)
            value--;
        if (valueStr.equals("null"))
            value = 0;
        return value;
    }

    @Override
    public void calculation(RecordList rs, Long oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        super.setValueClass(Long.class);
        if (a.getDataType() == Object.class) return;
        if (a.getDataType() == String.class) {
            super.setValue(0L);
            return;
        }
        long digits = 0;
        for (Record r : rs) {
            int dig = calculateDigits(r.getField(a));
            if (dig > digits)
                digits = dig;
        }
        super.setValue(digits);
    }


    @Override
    public void update(RecordList rs) {
        calculation(rs, null);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Long> m, double threshold) {
        double rdpVal = ((Number) this.getValue()).doubleValue();
        double dpValue = ((Number) m.getValue()).doubleValue();

        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG)
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        return conf;
    }
}
