package dqm.jku.dqmeerkat.quality.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <h2>LEDCPIConfigCompoenent</h2>
 * <summary>Configuration for LEDC PI dataprofile generators. Can be used to create
 * {@link dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator} for the LEDCPI component</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 21.06.2022
 */
@Getter
@NoArgsConstructor
public class LEDCPIConfigComponent extends ConfigComponent {
    private String ledcPiId;
    private String ledcPiFilePath;
}
