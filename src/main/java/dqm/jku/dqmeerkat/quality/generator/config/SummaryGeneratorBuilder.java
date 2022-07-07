package dqm.jku.dqmeerkat.quality.generator.config;

import dqm.jku.dqmeerkat.quality.config.ConfigComponent;
import dqm.jku.dqmeerkat.quality.config.DataSummaryConfigComponent;
import dqm.jku.dqmeerkat.quality.generator.DataSummarySkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.MGSummarySkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.SpaceSavingSummarySkeletonGenerator;

import java.util.Optional;

/**
 * <h2>SummaryGeneratorBuilder</h2>
 * <summary>
 * {@link DataProfileSkeletonBuilder} implementation for generating {@link DataSummarySkeletonGenerator}s. Consumes
 * {@link DataSummaryConfigComponent} in order to generate different {@link DataSummarySkeletonGenerator} implementations,
 * depending on the {@link dqm.jku.dqmeerkat.quality.config.SummaryType} of the given {@link DataSummaryConfigComponent}.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
public class SummaryGeneratorBuilder implements DataProfileSkeletonBuilder<DataSummarySkeletonGenerator> {


    @Override
    public Optional<DataSummarySkeletonGenerator> fromConfig(ConfigComponent configComponent) {
        if (configComponent instanceof DataSummaryConfigComponent) {
            var summaryConfig = ((DataSummaryConfigComponent) configComponent);
            switch (summaryConfig.getSummaryType()) {
                case MG_SUMMARY:
                    return Optional.of(new MGSummarySkeletonGenerator(summaryConfig.getK()));
                case SPACE_SAVING_SUMMARY:
                    return Optional.of(new SpaceSavingSummarySkeletonGenerator(summaryConfig.getK()));
                default:
                    return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
