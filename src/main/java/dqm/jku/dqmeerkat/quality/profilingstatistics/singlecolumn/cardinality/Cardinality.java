package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.NumberProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.Constants;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.HashSet;
import java.util.Set;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.cardCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.card;


/**
 * Describes the metric Cardinality, the amount of different values in a data
 * set
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/cardinality/Cardinality")
public class Cardinality extends NumberProfileStatistic<Long, Long> {

    public Cardinality(DataProfile d) {
        super(card, cardCat, d, Long.class);
    }

    @Override
    public void calculation(RecordList rs, Long oldVal) {
        Attribute a = (Attribute) super.getRefElem();
        Set<Number> set = new HashSet<Number>();
        Set<String> strSet = new HashSet<String>();
        for (Record r : rs) {
            Number field;
            if (a.getDataType().equals(String.class) && r.getField(a) != null) strSet.add(r.getField(a).toString());
            else {
                field = (Number) r.getField(a);
                if (field != null) set.add(field);
            }
        }
        if (a.getDataType().equals(String.class)) {
            this.setValue((long) strSet.size());
        } else {
            this.setValue((long) set.size());
        }
        this.setInputValueClass(Long.class);
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
    public boolean checkConformance(ProfileStatistic<Long, Long> m, double threshold) {
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
