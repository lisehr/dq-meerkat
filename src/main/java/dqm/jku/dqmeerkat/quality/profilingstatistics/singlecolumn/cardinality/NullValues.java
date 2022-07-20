package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.LongResultProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.cardCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.nullVal;


/**
 * Describes the metric Null Values, the amount of empty fields in a Attribute
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/cardinality/NullValues")
public class NullValues extends LongResultProfileStatistic<Long> {

    public NullValues(DataProfile d) {
        super(nullVal, cardCat, d, Long.class);
    }

    @Override
    public void calculation(RecordList rs, Long oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        long nullVals = 0;
        for (Record r : rs) {
            if (r.getField(a) == null) nullVals++;
        }
        this.setValue(nullVals);
        this.setInputValueClass(Long.class);
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

}
