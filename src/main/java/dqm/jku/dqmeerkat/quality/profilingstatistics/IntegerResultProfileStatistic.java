package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;

import java.util.Objects;

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

    @Override
    public boolean checkConformance(ProfileStatistic<TIn, Integer> m, double threshold) {
        long rdpVal = Objects.requireNonNullElse(getValue(), getDefaultRDPVal());
        long dpValue = Objects.requireNonNullElse(m.getValue(), getDefaultRDPVal());

        rdpVal = rdpVal - (int) (Math.abs(rdpVal) * threshold);    // shift by threshold
        boolean conf = dpValue >= rdpVal;
        if (!conf && Constants.DEBUG) {
            System.out.println(StatisticTitle.min + " exceeded: " + dpValue + " < " + rdpVal + " (originally: " +
                    this.getValue() + ")");
        }
        return conf;
    }
}
