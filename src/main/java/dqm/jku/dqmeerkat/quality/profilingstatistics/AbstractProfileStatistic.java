package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import org.cyberborean.rdfbeans.annotations.RDF;

import java.util.List;
import java.util.Objects;

/**
 * Abstract class describing the basic structure for a profilestatistic
 *
 * @author optimusseptim
 */
public abstract class AbstractProfileStatistic extends ProfileStatistic<Object> {


    public AbstractProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        super(title, cat, refProf);
    }


    @Override
    public int hashCode() {
        return Objects.hash(title, valueClass, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof AbstractProfileStatistic)) return false;
        AbstractProfileStatistic other = (AbstractProfileStatistic) obj;
        return Objects.equals(title, other.title) && Objects.equals(valueClass, other.valueClass) && Objects.equals(value, other.value);
    }



}
