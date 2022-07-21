package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;

/**
 * <h2>DependentNumberProfileStatistic</h2>
 * <summary>Base class for Profilestatistics, that depend on each other und use {@link Number}s for data input
 * and resulting output
 * </summary>
 *
 * @param <TIn>  the type of the input data, which has to extend from {@link Number}
 * @param <TOut> the type of the output data, which has to extend from {@link Number} and is set in value
 * @author meindl, rainer.meindl@scch.at
 * @since 12.07.2022
 */
public abstract class DependentNumberProfileStatistic<TIn extends Number, TOut extends Number> extends DependentProfileStatistic<TIn, TOut> {

    public DependentNumberProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf,
                                           Class<TIn> genericType) {
        super(title, cat, refProf, genericType);
    }

    protected boolean ensureDataTypeCorrect(Class<?> type) {
        return type.isAssignableFrom(inputValueClass);
    }

    protected abstract TIn getBasicInstance();

    protected abstract TOut getDefaultRDPVal();


}
