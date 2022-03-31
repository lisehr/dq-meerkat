package dqm.jku.dqmeerkat.domain.dtdl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>DTDLInterface</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl
 * @since 17.03.2022
 */
@Data
@NoArgsConstructor
public class DtdlInterface {
    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;

    private List<DtdlObject> contents = new ArrayList<>();

    @JsonProperty("@context")
    private String context = "dtmi:at:scch:dsd:Concept;1";

    @JsonProperty("extends")
    private List<String> extendz = new ArrayList<>();

    private String displayName;

    private String comment;

    public DtdlInterface(String id) {
        this.id = id;
    }
}
