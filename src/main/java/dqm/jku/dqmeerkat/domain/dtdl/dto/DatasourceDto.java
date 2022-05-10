package dqm.jku.dqmeerkat.domain.dtdl.dto;

import lombok.Data;

import java.util.UUID;

/**
 * <h2>DatasourceDto</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@Data
public class DatasourceDto {

    private UUID dtId;
    private UUID eTag;
    private MetaDataDto metaData;
}
