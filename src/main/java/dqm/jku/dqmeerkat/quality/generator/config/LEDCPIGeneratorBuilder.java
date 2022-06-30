package dqm.jku.dqmeerkat.quality.generator.config;

import dqm.jku.dqmeerkat.quality.config.ConfigComponent;
import dqm.jku.dqmeerkat.quality.config.LEDCPIConfigComponent;
import dqm.jku.dqmeerkat.quality.generator.LEDCPIGenerator;

import java.util.Optional;

/**
 * <h2>LEDCPIGeneratorBuilder</h2>
 * <summary>
 * {@link DataProfileSkeletonBuilder} implementation for {@link LEDCPIGenerator}s. Creates an instance of
 * {@link LEDCPIGenerator} if the given {@link ConfigComponent} is an instance of {@link LEDCPIConfigComponent}.
 * It extracts the necessary configuration for the {@link LEDCPIGenerator} fom the given {@link ConfigComponent}.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 21.06.2022
 */
public class LEDCPIGeneratorBuilder implements DataProfileSkeletonBuilder<LEDCPIGenerator> {
    @Override
    public Optional<LEDCPIGenerator> fromConfig(ConfigComponent configComponent) {
        if (configComponent instanceof LEDCPIConfigComponent) {
            var ledcConfig = ((LEDCPIConfigComponent) configComponent);
            return Optional.of(new LEDCPIGenerator(ledcConfig.getLedcPiId(), ledcConfig.getLedcPiFilePath()));
        }
        return Optional.empty();
    }
}
