package dqm.jku.dqmeerkat.quality.generator.config;

import dqm.jku.dqmeerkat.quality.config.ConfigComponent;
import dqm.jku.dqmeerkat.quality.config.FullProfileConfigComponent;
import dqm.jku.dqmeerkat.quality.generator.FullSkeletonGenerator;

import java.util.Optional;

/**
 * <h2>FullSkeletonGeneratorBuilder</h2>
 * <summary>
 * {@link DataProfileSkeletonBuilder} implementation for {@link FullSkeletonGenerator}s. Creates an instance of
 * {@link FullSkeletonGenerator} if the given {@link ConfigComponent} is an instance of
 * {@link FullProfileConfigComponent}
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 21.06.2022
 */
public class FullSkeletonGeneratorBuilder implements DataProfileSkeletonBuilder<FullSkeletonGenerator> {
    @Override
    public Optional<FullSkeletonGenerator> fromConfig(ConfigComponent configComponent) {
        if (configComponent instanceof FullProfileConfigComponent)
            return Optional.of(new FullSkeletonGenerator()); // pretty easy, nothing to config as of yet
        return Optional.empty();
    }
}
