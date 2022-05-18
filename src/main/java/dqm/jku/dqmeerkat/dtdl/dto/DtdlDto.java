package dqm.jku.dqmeerkat.dtdl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DatasourceDto.class, name = "datasource"),
        @JsonSubTypes.Type(value = ProfileStatisticDto.class, name = "profilestatistic")
})
@SuperBuilder
@Data
public class DtdlDto {
    @Builder.Default
    @JsonProperty("$dtId")
    protected UUID dtId = UUID.randomUUID();
    @Builder.Default
    @JsonProperty("$etag")
    protected UUID eTag = UUID.randomUUID();
    @JsonProperty("$metadata")
    protected MetaDataDto metaData;
}
