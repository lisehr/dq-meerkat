package dqm.jku.dqmeerkat.quality.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <h2>ConfigComponent</h2>
 * Deserialized part of the {@link DataProfileConfiguration}, which includes one subpart of the whole config.
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.06.2022
 */
@Data
class ConfigComponent {
    private String ledcPiId;
    private String ledcPiFilePath;
}
