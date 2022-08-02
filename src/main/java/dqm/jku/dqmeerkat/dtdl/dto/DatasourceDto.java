package dqm.jku.dqmeerkat.dtdl.dto;

import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import lombok.experimental.SuperBuilder;

/**
 * <h2>DatasourceDto</h2>
 * <summary>
 * Data Transfer Object for {@link dqm.jku.dqmeerkat.dsd.elements.Datasource} instances.
 * The {@link AbstractProfileStatistic}s are transformed in their own
 * DTO, the {@link ProfileStatisticDto} and linked together using multiple {@link RelationshipDto}s.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@SuperBuilder
public class DatasourceDto extends DtdlDto {

}
