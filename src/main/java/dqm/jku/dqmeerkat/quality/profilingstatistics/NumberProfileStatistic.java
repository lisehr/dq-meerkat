package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;

/**
 * <h2>NumberProfileStatistic</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 12.07.2022
 */
public abstract class NumberProfileStatistic<TIn extends Number, TOut extends Number> extends ProfileStatistic<TIn, TOut> {

    protected NumberProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf, Class<TIn> genericType) {
        super(title, cat, refProf);
        this.inputValueClass = genericType;
    }

    protected boolean ensureDataTypeCorrect(Class<?> type) {
        return type.isAssignableFrom(inputValueClass);
    }
}
