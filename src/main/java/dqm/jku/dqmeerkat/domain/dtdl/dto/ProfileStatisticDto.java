package dqm.jku.dqmeerkat.domain.dtdl.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * <h2>ProfileStatisticDto</h2>
 * <summary>
 *     TODO Insert doc header and implement
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@SuperBuilder
@Data
public class ProfileStatisticDto extends DtdlDto {
    private String title;
    private String value;
    private String category;
}
