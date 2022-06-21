package dqm.jku.dqmeerkat.quality.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * <h2>ConfigComponent</h2>
 * Deserialized part of the {@link DataProfileConfiguration}, which includes one subpart of the whole config.
 *
 * @author meindl, rainer.meindl@scch.at
 * @implNote all subclasses need to have both a default constructor and getters for the properties.
 * Otherwise, subclasses can't be deserialized
 * @since 20.06.2022
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LEDCPIConfigComponent.class, name = "ledcpi"),
        @JsonSubTypes.Type(value = FullProfileConfigComponent.class, name = "full")
})
@Data
class ConfigComponent {
}
