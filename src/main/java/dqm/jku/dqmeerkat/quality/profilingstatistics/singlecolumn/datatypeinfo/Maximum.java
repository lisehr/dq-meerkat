package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.NumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.util.Constants;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.max;


/**
 * Describes the metric Maximum, which denotes the maximum value in an
 * Attribute.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Maximum")
public class Maximum extends NumberProfileStatistic<Double> {
    public Maximum(DataProfile d) {
        super(max, dti, d, Double.class);
    }

    @Override
    public void calculation(RecordList rs, Double oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        double val = Objects.requireNonNullElse(oldVal, getBasicInstance());
        if (ensureDataTypeCorrect(a.getDataType())) {
            for (Record r : rs) {
                var field = r.getField(a);
                if (field == null) {
                    continue;
                }
                val = getMaximum(val, (double) field);
            }
        } else {
            LOGGER.warn("Attribute {} has wrong data type {} for {}: {}. Skipping calculation.", a.getLabel(), a.getDataType(),
                    getClass().getSimpleName(), valueClass.getSimpleName());
        }
        this.setValue(val);
    }

    /**
     * Creates a basic instance used as a reference (in this case the minimum value)
     *
     * @return the reference value
     */
    protected Double getBasicInstance() {
        return Double.MIN_VALUE;
    }

    /**
     * Checks the maximum value of two objects
     *
     * @param current the current maximum value
     * @param toComp  the new value to compare
     * @return the new maximum value
     */
    private Double getMaximum(Double current, Double toComp) {
        if (toComp == null) {
            return current; // if the maximum of the former processed records is null, this record is the maximum
        }
        Attribute a = (Attribute) super.getRefElem();
        // TODO move to string implementation
//        if (a.getDataType().equals(String.class) && !isNumericList) {
//            return Integer.max((int) current, ((String) toComp).length());

        return Double.max(current, ((Number) toComp).doubleValue());


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
    public boolean checkConformance(ProfileStatistic<Double> m, double threshold) {
        double rdpVal;
        if (getValue() == null) {
            rdpVal = 0;
        } else {
            rdpVal = ((Number) this.getValue()).doubleValue();
        }
        double dpValue = ((Number) m.getValue()).doubleValue();

        rdpVal = rdpVal + (Math.abs(rdpVal) * threshold);    // shift by threshold
        boolean conf = dpValue <= rdpVal;
        if (!conf && Constants.DEBUG) {
            System.out.println(StatisticTitle.max + " exceeded: " + dpValue + " > " + rdpVal + " (originally: " + this.getValue() + ")");
        }
        return conf;

    }
}
