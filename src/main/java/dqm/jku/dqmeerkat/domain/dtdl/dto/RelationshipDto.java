package dqm.jku.dqmeerkat.domain.dtdl.dto;

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
public class RelationshipDto {
    private UUID relationshipId;
    private UUID sourceId;
    private UUID targetId;
    private String relationshipName;

}
