package dqm.jku.dqmeerkat.domain.dtdl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

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
public class DatasourceGraph {


    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<DtdlDto> digitalTwins;

    private List<RelationshipDto> relationships;

}
