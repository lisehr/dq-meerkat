package dqm.jku.dqmeerkat.quality.generator.config;

import dqm.jku.dqmeerkat.quality.config.ConfigComponent;
import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;

import java.util.Optional;

/**
 * <h2>DataProfileSkeletonBuilder</h2>
 * <p>
 * This interface defines logic for constructing components that in turn construct DataProfiles. It processes an
 * implementation of {@link ConfigComponent} and returns an instance of {@link DataProfileSkeletonGenerator} if
 * the given {@link ConfigComponent} is an instance compatible with the implementation. The {@link ConfigComponent}
 * implementation should hold all necessary information to correctly instantiate the {@link DataProfileSkeletonGenerator}
 * </p>
 *
 * @param <T> the actual implementation of {@link DataProfileSkeletonGenerator} to be constructed
 */
@FunctionalInterface
public interface DataProfileSkeletonBuilder<T extends DataProfileSkeletonGenerator> {

    /**
     * <p>
     * Creates an instance of {@link DataProfileSkeletonGenerator} if the given {@link ConfigComponent} is an
     * instance of {@link ConfigComponent} compatible with the implementation of this interface.
     * </p>
     *
     * @param configComponent a deserialized component of the dq configuration. The implementation of this interface
     *                        provides the necessary information to correctly instantiate the
     *                        {@link DataProfileSkeletonGenerator}
     * @return a new instance of {@link DataProfileSkeletonGenerator} if the given {@link ConfigComponent} is of the
     * correct type. Otherwise, an empty {@link Optional} is returned.
     */
    Optional<T> fromConfig(ConfigComponent configComponent);
}
