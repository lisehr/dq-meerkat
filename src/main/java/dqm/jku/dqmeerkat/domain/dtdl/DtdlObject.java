package dqm.jku.dqmeerkat.domain.dtdl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>DTDLObject</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl
 * @since 17.03.2022
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtdlObject {
    @JsonProperty("@type")
    private String type;

    private String name;

    @Builder.Default
    private String target = null;

    @Builder.Default
    private String comment = null;

    @Builder.Default
    private String displayName = null;

    @Builder.Default
    private String schema = null;
}
