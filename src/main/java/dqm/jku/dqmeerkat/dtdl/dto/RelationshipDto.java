package dqm.jku.dqmeerkat.dtdl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * <h2>RelationshipDto</h2>
 * <summary>
 * Data Transfer Object for relationships between two Digital twins. For example between {@link DatasourceDto} and
 * {@link ProfileStatisticDto}.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@Data
@Builder
public class RelationshipDto {
    @Builder.Default
    @JsonProperty("$etag")
    private UUID eTag = UUID.randomUUID();
    @Builder.Default
    @JsonProperty("$relationshipId")
    private UUID relationshipId = UUID.randomUUID();
    @JsonProperty("$sourceId")
    private UUID sourceId;
    @JsonProperty("$targetId")
    private UUID targetId;
    @JsonProperty("$relationshipName")
    private String relationshipName;

}
