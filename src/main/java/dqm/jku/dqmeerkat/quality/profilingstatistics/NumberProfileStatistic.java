package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;

/**
 * <h2>NumberProfileStatistic</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 12.07.2022
 */
public abstract class NumberProfileStatistic<T extends Number> extends ProfileStatistic<T> {

    protected NumberProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf, Class<T> genericType) {
        super(title, cat, refProf);
        this.valueClass = genericType;
    }

    protected boolean ensureDataTypeCorrect(Class<?> type) {
        return type.isAssignableFrom(valueClass);
    }
}
