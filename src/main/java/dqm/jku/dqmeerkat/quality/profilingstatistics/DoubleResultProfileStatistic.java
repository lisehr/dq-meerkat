package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;

import java.util.Objects;

/**
 * <h2>DoubleResultProfileStatistic</h2>
 * <summary>
 * {@link NumberProfileStatistic} implementation, fixing the output type to {@link Double}. This allows easier
 * implementations for subclasses and reduces code duplication.
 * </summary>
 *
 * @param <TIn> The input type of the ProfileStatistic, i.E. what can be handled by this class
 * @author meindl, rainer.meindl@scch.at
 * @since 20.07.2022
 */
public abstract class DoubleResultProfileStatistic<TIn extends Number> extends NumberProfileStatistic<TIn, Double> {
    protected DoubleResultProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf, Class<TIn> genericType) {
        super(title, cat, refProf, genericType);
    }

    @Override
    protected Double getDefaultRDPVal() {
        return 0D;
    }

    @Override
    public boolean checkConformance(ProfileStatistic<TIn, Double> m, double threshold) {
        double rdpVal = Objects.requireNonNullElse(getValue(), getDefaultRDPVal());
        double dpValue = Objects.requireNonNullElse(m.getValue(), getDefaultRDPVal());

        rdpVal = rdpVal - (Math.abs(rdpVal) * threshold);    // shift by threshold
        boolean conf = dpValue >= rdpVal;
        if (!conf && Constants.DEBUG) {
            System.out.println(StatisticTitle.min + " exceeded: " + dpValue + " < " + rdpVal + " (originally: " +
                    this.getValue() + ")");
        }
        return conf;
    }
}
