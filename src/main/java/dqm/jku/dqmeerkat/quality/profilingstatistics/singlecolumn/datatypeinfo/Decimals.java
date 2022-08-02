package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.dec;

/**
 * Describes the metric Decimals, which are the amount of digits after the
 * decimal point.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Decimals")
public class Decimals extends ProfileStatistic<Integer, Integer> {

    public Decimals(DataProfile d) {
        super(dec, dti, d, Integer.class);
    }

    @Override
    public void calculation(RecordList rs, Integer oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        this.setInputValueClass(Integer.class);
        if (a.getDataType() == Object.class) return;
        if (a.getDataType() == Integer.class || a.getDataType() == Long.class || a.getDataType() == String.class) {
            this.setValue(0);
            return;
        }

        int decimals;
        decimals = Objects.requireNonNullElse(oldVal, 0);

        for (Record r : rs) {
            Object field = r.getField(a);
            decimals = getDecimals(decimals, (Number) field);
        }
        if (decimals == -1) this.setValue(null);
        else {
            this.setValue(decimals);
        }
    }

    private int getDecimals(int decimals, Number field) {
        if (field == null)
            return 0;
        String numStr = field.toString();
        if (StringUtils.isBlank(numStr) || numStr.isEmpty())
            return -1;
        int pointPos = numStr.indexOf('.');
        int dec = numStr.length() - pointPos - 1;
        return Math.max(dec, decimals);
    }


    @Override
    public void update(RecordList rs) {
        calculation(rs, super.getValue());
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Integer, Integer> m, double threshold) {
        if (getValue() == null)
            setValue(m.getValue());
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
