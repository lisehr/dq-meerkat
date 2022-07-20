package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;

import java.util.Objects;

/**
 * <h2>DependentIntegerResultProfileStatistic</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.07.2022
 */
public abstract class DependentLongResultProfileStatistic<TIn extends Number> extends DependentNumberProfileStatistic<TIn, Long> {
    protected DependentLongResultProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf, Class<TIn> genericType) {
        super(title, cat, refProf, genericType);
    }

    @Override
    protected Long getDefaultRDPVal() {
        return 0L;
    }


    @Override
    public boolean checkConformance(ProfileStatistic<TIn, Long> m, double threshold) {
        long rdpVal = Objects.requireNonNullElse(getValue(), getDefaultRDPVal());
        long dpValue = Objects.requireNonNullElse(m.getValue(), getDefaultRDPVal());

        rdpVal = rdpVal - (long) (Math.abs(rdpVal) * threshold);    // shift by threshold
        boolean conf = dpValue >= rdpVal;
        if (!conf && Constants.DEBUG) {
            System.out.println(StatisticTitle.min + " exceeded: " + dpValue + " < " + rdpVal + " (originally: " +
                    this.getValue() + ")");
        }
        return conf;
    }
}
