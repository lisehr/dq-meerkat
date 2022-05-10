package dqm.jku.dqmeerkat.domain.dtdl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>DtdlGraphWrapper</h2>
 * <summary>Wrapper for {@link DtdlGraph}, necessary to comply to the REST interface by tributech. Contains
 * meta information describing the file version</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@Data
@NoArgsConstructor
public class DtdlGraphWrapper {

    // force jackson to generate a nested element
    @AllArgsConstructor
    @Data
    static class FileInfo {
        private String fileVersion;

    }

    public DtdlGraphWrapper(DtdlGraph digitalTwinsGraph) {
        this.digitalTwinsGraph = digitalTwinsGraph;
    }

    private FileInfo digitalTwinsFileInfo = new FileInfo("1.0");
    private DtdlGraph digitalTwinsGraph;
}
