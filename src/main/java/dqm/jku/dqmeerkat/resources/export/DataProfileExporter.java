package dqm.jku.dqmeerkat.resources.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.domain.dtdl.DtdlInterface;
import dqm.jku.dqmeerkat.domain.dtdl.DtdlObject;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import lombok.SneakyThrows;

import java.util.List;

/**
 * <h2>DataProfileExporter</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 29.04.2022
 */
public class DataProfileExporter implements AbstractExporter<DataProfile> {
    @SneakyThrows
    @Override
    public void export(DataProfile toExport, String fileName) {
        var dtdlInterface = new DtdlInterface("dtmi:scch:at:dq:Dataprofile;1");
        dtdlInterface.setDisplayName("Data Profile");
        dtdlInterface.getContents().add(DtdlObject.builder()
                .name("statistics")
                .displayName("Profile Statistics")
                .type("Relationship")
                .target("dtmi:scch:at:dq:ProfileStatistics;1")
                .build()
        );
        ObjectMapper mapper = new ObjectMapper();
        var ret = mapper.writeValueAsString(dtdlInterface);
        System.out.println(ret);
    }
}
