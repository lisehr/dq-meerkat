package dqm.jku.dqmeerkat.quality.config;

import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * <h2>DataProfileConfiguration</h2>
 * <summary>Configuration class for {@link dqm.jku.dqmeerkat.quality.DataProfile}. Contains necessary
 * information such as how they look like in terms of selected
 * {@link dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic}s</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 14.06.2022
 */
@AllArgsConstructor
@Data
public class DataProfileConfiguration {

    private final List<DataProfileSkeletonGenerator> generators;
}
