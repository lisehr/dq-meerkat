package dqm.jku.dqmeerkat.quality.generator.config;

import dqm.jku.dqmeerkat.quality.config.ConfigComponent;
import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;

import java.util.Optional;

@FunctionalInterface
public interface DataProfileSkeletonBuilder<T extends DataProfileSkeletonGenerator> {
    Optional<T> fromConfig(ConfigComponent configComponent);
}
