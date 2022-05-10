package dqm.jku.dqmeerkat.domain.dtdl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * <h2>DatasourceDto</h2>
 * <summary>
 * Data Transfer Object for {@link dqm.jku.dqmeerkat.dsd.elements.Datasource} instances.
 * The {@link dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic}s are transformed in their own
 * DTO, the {@link ProfileStatisticDto} and linked together using multiple {@link RelationshipDto}s.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@Data
@Builder
public class DatasourceDto {

    @Builder.Default
    @JsonProperty("$dtId")
    private UUID dtId = UUID.randomUUID();
    @Builder.Default
    @JsonProperty("$eTag")
    private UUID eTag = UUID.randomUUID();
    @JsonProperty("$metadata")
    private MetaDataDto metaData;


}
