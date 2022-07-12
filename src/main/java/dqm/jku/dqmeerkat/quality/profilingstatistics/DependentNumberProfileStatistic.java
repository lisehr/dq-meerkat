package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;

/**
 * <h2>DependentNumberProfileStatistic</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 12.07.2022
 */
public abstract class DependentNumberProfileStatistic<T extends Number> extends DependentProfileStatistic<T> {
    public DependentNumberProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        super(title, cat, refProf);
    }

    protected abstract T getBasicInstance();

}