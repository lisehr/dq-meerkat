package dqm.jku.dqmeerkat.domain.dtdl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
@SuperBuilder
public class DatasourceDto extends DtdlDto {

}
