package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;

/**
 * Second hierarchy level of abstract base class for profile statistics, its main
 * use is to provide methods for including dependent statistics.
 *
 * @author optimusseptim
 */
public abstract class DependentProfileStatistic<TIn, TOut> extends ProfileStatistic<TIn, TOut> {


    public DependentProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        super(title, cat, refProf);
        this.dependencyCheck();
    }

    /**
     * Helper method to calculate missing dependencies in calculation with a list of
     * numbers as base. Can be empty in Metrics without a dependency.
     *
     * @param list the numeric value list
     * @throws NoSuchMethodException
     */
//    protected abstract void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException;

    /**
     * Helper method to calculate missing dependencies in calculation with a
     * RecordList as base. Can be empty in Metrics without a dependency.
     *
     * @param rl the recordlist
     */
    protected abstract void dependencyCalculationWithRecordList(RecordList rl);

    /**
     * Helper method to add missing dependencies needed for metric calculation
     */
    protected abstract void dependencyCheck();
}
