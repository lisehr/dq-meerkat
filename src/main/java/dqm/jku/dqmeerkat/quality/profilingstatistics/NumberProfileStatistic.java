package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;

/**
 * <h2>NumberProfileStatistic</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 12.07.2022
 */
public abstract class NumberProfileStatistic<T extends Number> extends ProfileStatistic<T>{
    public NumberProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        super(title, cat, refProf);
    }

    protected abstract T getBasicInstance();
}
