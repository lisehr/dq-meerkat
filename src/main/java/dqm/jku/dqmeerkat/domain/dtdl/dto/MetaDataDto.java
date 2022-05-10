package dqm.jku.dqmeerkat.domain.dtdl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>MetaData</h2>
 * <summary>Nested DTO for any digital twin instances, such as {@link DatasourceDto}</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataDto {
    @JsonProperty("$model")
    private String model;
}
