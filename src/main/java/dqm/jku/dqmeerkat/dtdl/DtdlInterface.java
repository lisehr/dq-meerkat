package dqm.jku.dqmeerkat.dtdl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
/**
 * <h2>DTDLInterface</h2>
 * <summary>Domain class representing a DTDL Interface definition</summary>
 *
 * @author meindl
 * @since 17.03.2022
 * @see <a href="https://github.com/Azure/opendigitaltwins-dtdl/blob/master/DTDL/v2/dtdlv2.md">DTDL Definition</a>
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtdlInterface {
    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type = "Interface";

    private List<DtdlObject> contents = new ArrayList<>();

    @JsonProperty("@context")
    private String context = "dtmi:dtdl:context;2";

    @JsonProperty("extends")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> extendz = new ArrayList<>();

    private String displayName;

    private String comment;

    public DtdlInterface(String id) {
        this.id = id;
    }
}
