package dqm.jku.dqmeerkat.domain.dtdl;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <h2>DTDLInterface</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl
 * @since 17.03.2022
 */
public class DtdlInterface {
    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;

    private List<DtdlObject> contents;

    @JsonProperty("@context")
    private String context;

    @JsonProperty("extends")
    private List<String> extendz;

    private String displayName;

    private String comment;

    public DtdlInterface(String id) {
        this.id = id;
    }
}
