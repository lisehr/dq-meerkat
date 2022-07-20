package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.LongResultProfileStatistic;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.cardCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numrows;


/**
 * Describes the metric NumRows, which is the amount of rows in a data set.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/cardinality/Cardinality")
public class NumRows extends LongResultProfileStatistic<Long> {

    public NumRows(DataProfile d) {
        super(numrows, cardCat, d, Long.class);
    }

    @Override
    public void calculation(RecordList rs, Long oldVal) {
        super.setValue((long) rs.size());
        super.setInputValueClass(Long.class); // This was Integer and is now set to Long - if errors occur change it back!
    }

    @Override
    public void update(RecordList rs) {
        var oldSize = super.getValue();
        super.setValue(oldSize + rs.size());
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<Long, Long> m, double threshold) {        // Never evaluates to false, because the reference is here the size of the RDP and should not be compared to the batch size of the DPs
        return true;
    }
}
