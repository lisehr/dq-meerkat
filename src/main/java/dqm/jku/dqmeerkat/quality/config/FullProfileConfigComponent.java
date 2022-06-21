package dqm.jku.dqmeerkat.quality.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <h2>FullProfileConfigComponent</h2>
 * Example configuration component for generating a {@link dqm.jku.dqmeerkat.quality.generator.FullSkeletonGenerator}.
 * As there is no real configuration for this component it only contains a property  {@code someParameter} for
 * evaluation purposes.
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 21.06.2022
 */
@Getter
@NoArgsConstructor
public class FullProfileConfigComponent extends ConfigComponent {
    private String someParameter;
}
