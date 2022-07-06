package dqm.jku.dqmeerkat.quality.generator.config;

import dqm.jku.dqmeerkat.quality.config.ConfigComponent;
import dqm.jku.dqmeerkat.quality.config.DataSummaryConfigComponent;
import dqm.jku.dqmeerkat.quality.generator.DataSummarySkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.MGSummarySkeletonGenerator;

import java.util.Optional;

/**
 * <h2>SummaryGeneratorBuilder</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
public class SummaryGeneratorBuilder implements DataProfileSkeletonBuilder<DataSummarySkeletonGenerator> {


    @Override
    public Optional<DataSummarySkeletonGenerator> fromConfig(ConfigComponent configComponent) {
        if (configComponent instanceof DataSummaryConfigComponent)
            return Optional.of(new MGSummarySkeletonGenerator(((DataSummaryConfigComponent) configComponent).getK()));
        return Optional.empty();
    }
}
