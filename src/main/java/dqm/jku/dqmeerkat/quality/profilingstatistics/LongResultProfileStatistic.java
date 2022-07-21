package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;

import java.util.Objects;

/**
 * <h2>LongMetricProfileStatistic</h2>
 * <summary>
 * {@link NumberProfileStatistic} implementation, fixing the output type to {@link Long}. This allows easier
 * implementations for subclasses and reduces code duplication.
 * </summary>
 *
 * @param <TIn> The input type of the ProfileStatistic, i.E. what can be handled by this class
 * @author meindl, rainer.meindl@scch.at
 * @since 20.07.2022
 */
public abstract class LongResultProfileStatistic<TIn extends Number> extends NumberProfileStatistic<TIn, Long> {
    protected LongResultProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf, Class<TIn> genericType) {
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
