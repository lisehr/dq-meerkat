package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;

import java.util.Objects;

/**
 * Abstract class describing the basic structure for a profilestatistic
 *
 * @author optimusseptim
 */
public abstract class AbstractProfileStatistic extends ProfileStatistic<Object, Object> {


    public AbstractProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        super(title, cat, refProf, Object.class);
    }


    @Override
    public int hashCode() {
        return Objects.hash(title, inputValueClass, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof AbstractProfileStatistic)) return false;
        AbstractProfileStatistic other = (AbstractProfileStatistic) obj;
        return Objects.equals(title, other.title) && Objects.equals(inputValueClass, other.inputValueClass) && Objects.equals(value, other.value);
    }


}
