package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;

/**
 * <h2>NumberProfileStatistic</h2>
 * <summary>
 * {@link ProfileStatistic} subclass for {@link Number} values. This abstact class defines methods for default values,
 * which can be used for generic abstractions.
 * </summary>
 *
 * @param <TIn>  The input type of the ProfileStatistic, i.E. what can be handled by this class
 * @param <TOut> The output type of the ProfileStatistic, i.E. what type is produced by this class and provided by value
 * @author meindl, rainer.meindl@scch.at
 * @since 12.07.2022
 */
public abstract class NumberProfileStatistic<TIn extends Number, TOut extends Number> extends ProfileStatistic<TIn, TOut> {

    protected NumberProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf, Class<TIn> genericType) {
        super(title, cat, refProf, genericType);
    }

    /**
     * returns the default value for the output type. This is used for generic abstractions.
     *
     * @return the default value for the output type
     */
    protected abstract TOut getDefaultRDPVal();


}
