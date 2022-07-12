package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.Constants;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.cardCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.nullVal;


/**
 * Describes the metric Null Values, the amount of empty fields in a Attribute
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/cardinality/NullValues")
public class NullValues extends AbstractProfileStatistic {

    public NullValues(DataProfile d) {
        super(nullVal, cardCat, d);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        long nullVals = 0;
        for (Record r : rs) {
            if (r.getField(a) == null) nullVals++;
        }
        this.setValue(nullVals);
        this.setNumericVal((Number) nullVals);
        this.setValueClass(Long.class);
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
        throw new NoSuchMethodException("Method not allowed for numeric lists!");
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, super.getValue());
    }

    @Override
    protected String getValueString() {
        if (getValue() == null) return "\tnull";
        return "\t" + getValue().toString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Object> m, double threshold) {
        double rdpVal = ((Number) this.getNumericVal()).doubleValue();
        double dpValue = ((Number) m.getValue()).doubleValue();

        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG)
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        return conf;
    }
}
