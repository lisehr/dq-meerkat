package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;

/**
 * <h2>IntegerMetricProfileStatistic</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.07.2022
 */
public abstract class IntegerResultProfileStatistic<TIn extends Number> extends NumberProfileStatistic<TIn, Integer> {
    protected IntegerResultProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf, Class<TIn> genericType) {
        super(title, cat, refProf, genericType);
    }

    @Override
    protected Integer getDefaultRDPVal() {
        return 0;
    }
}
