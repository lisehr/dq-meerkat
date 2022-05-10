package dqm.jku.dqmeerkat.domain.dtdl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * <h2>DatasourceGraph</h2>
 * <summary>
 * DTO for a graph of {@link DatasourceDto}, its {@link ProfileStatisticDto}s and corresponding
 * {@link RelationshipDto}s
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@Data
public class DtdlGraph {


    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Set<DtdlDto> digitalTwins = new HashSet<>();

    private Set<RelationshipDto> relationships = new HashSet<>();


    /**
     * <p>
     * Adds the given {@link DtdlDto} twin to the graph. The twin in this state does not have any relationships yet
     * </p>
     *
     * @param twin the dtdl twin in its dto form
     */
    public void addDigitalTwin(DtdlDto twin) {
        digitalTwins.add(twin);
    }

    /**
     * <p>
     * Adds the given relationship between two twins to the graph. Constraints ensure consistency of the graph, i.E.
     * the relationship to add must reference twins, that have previously been added to the graph. These twins are
     * identified by their dtdId, which has to correspond to either the source-, or targetId
     * </p>
     *
     * @param relationship the relationship to add as a dto.
     */
    public boolean addRelationship(RelationshipDto relationship) {
        if (digitalTwins.stream()
                .map(DtdlDto::getDtId)
                .anyMatch(uuid -> relationship.getSourceId().equals(uuid)) &&
                digitalTwins.stream()
                        .map(DtdlDto::getDtId)
                        .anyMatch(uuid -> relationship.getTargetId().equals(uuid))) {
            return relationships.add(relationship);
        }
        return false;
    }
}
