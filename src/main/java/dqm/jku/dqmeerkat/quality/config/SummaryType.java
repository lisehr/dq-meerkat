package dqm.jku.dqmeerkat.quality.config;

/**
 * <h2>DataSummaryConfigComponent</h2>
 * <summary>
 * Allows for differentiation between which {@link dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.summary.SummaryProfileStatistic}
 * should be deserialized in the config.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
public enum SummaryType {
    MG_SUMMARY, SPACE_SAVING_SUMMARY
}
