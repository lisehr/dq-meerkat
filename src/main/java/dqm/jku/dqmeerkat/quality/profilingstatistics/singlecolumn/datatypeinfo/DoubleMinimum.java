package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DoubleResultProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.Objects;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.min;


/**
 * Describes the metric Minimum, which is the minimum value of all values of an
 * Attribute.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Minimum")
public class DoubleMinimum extends DoubleResultProfileStatistic<Double> {


    public DoubleMinimum(DataProfile d) {
        super(min, dti, d, Double.class);
    }

    @Override
    public void calculation(RecordList rs, Double oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        var val = Objects.requireNonNullElse(oldVal, getBasicInstance());
        if (ensureDataTypeCorrect(a.getDataType())) {
            for (Record r : rs) {
                var field = r.getField(a);
                if (field == null) {
                    continue;
                }
                val = getMinimum(val, (double) r.getField(a));
            }
        } else {
            LOGGER.warn("Field {} is not of type Double for {}, skipping it...", a.getLabel(),
                    this.getClass().getSimpleName());
            return;
        }

        this.setValue(val);
    }

    /**
     * Creates a basic instance used as a reference (in this case the maximum value)
     *
     * @return the reference value
     */
    private Double getBasicInstance() {
        return Double.MAX_VALUE;
    }

    /**
     * Checks the minimum value of two objects
     *
     * @param current the current minimum value
     * @param toComp  the new value to compare
     * @return the new minimum value
     */
    private Double getMinimum(Double current, Double toComp) {
        if (toComp == null) {
            return current;
        }

        return Double.min(current, toComp);

        // TODO string implementation
//        if (a.getDataType().equals(String.class) && !isNumericList)
//            return Integer.min((int) current, ((String) toComp).length());

    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, super.getValue());
    }


    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

}
