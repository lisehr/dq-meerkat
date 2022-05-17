package dqm.jku.dqmeerkat.dtdl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>DTDLObject</h2>
 * <summary>Domain class containing any DTDL Object. Can be anything from property to a relationship. No
 * data is validated, i.E. a property can also have a target although this is not allowed in the DTDL Definition.
 * </summary>
 *
 * @author meindl
 * @see <a href="https://github.com/Azure/opendigitaltwins-dtdl/blob/master/DTDL/v2/dtdlv2.md">DTDL Definition</a>
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
