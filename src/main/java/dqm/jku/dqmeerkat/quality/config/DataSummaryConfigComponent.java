package dqm.jku.dqmeerkat.quality.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <h2>DataSummaryConfigComponent</h2>
 * <p>
 * {@link ConfigComponent} implementation for configuration of
 * {@link dqm.jku.dqmeerkat.quality.generator.DataSummarySkeletonGenerator}s.
 * </p>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataSummaryConfigComponent extends ConfigComponent {
    private int k;
    private SummaryType summaryType;
}
