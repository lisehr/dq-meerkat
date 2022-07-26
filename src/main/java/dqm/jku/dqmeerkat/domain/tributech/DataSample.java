package dqm.jku.dqmeerkat.domain.tributech;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * <h2>DataSample</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 26.07.2022
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DataSample<T> {
    private UUID valueMetadataId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.nXXX")
    private LocalDateTime timestamp;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.nXXX")
    private LocalDateTime createdAt;
    private int syncNr;
    private int size;
    private List<T> value;
}
