package dqm.jku.dqmeerkat.domain.dtdl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> extendz = new ArrayList<>();

    private String displayName;

    private String comment;

    public DtdlInterface(String id) {
        this.id = id;
    }
}