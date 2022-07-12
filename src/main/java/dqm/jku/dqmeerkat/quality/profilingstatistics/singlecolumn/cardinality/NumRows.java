package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.cardCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numrows;


/**
 * Describes the metric NumRows, which is the amount of rows in a data set.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/cardinality/Cardinality")
public class NumRows extends AbstractProfileStatistic {

    public NumRows(DataProfile d) {
        super(numrows, cardCat, d);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        super.setValue((long) rs.size());
        super.setNumericVal(((Number) rs.size()).longValue());
        super.setValueClass(Long.class); // This was Integer and is now set to Long - if errors occur change it back!
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) {
        super.setValue((long) list.size());
        super.setNumericVal(((Number) list.size()).longValue());
        super.setValueClass(Long.class); // This was Integer and is now set to Long - if errors occur change it back!
    }

    @Override
    public void update(RecordList rs) {
        int oldSize = (int) super.getValue();
        super.setValue(oldSize + rs.size());
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Object> m, double threshold) {        // Never evaluates to false, because the reference is here the size of the RDP and should not be compared to the batch size of the DPs
        return true;
    }
}
