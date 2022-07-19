package dqm.jku.dqmeerkat.resources.export.json.dtdl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.dtdl.DtdlInterface;
import dqm.jku.dqmeerkat.dtdl.DtdlObject;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.resources.export.SchemaExporter;
import lombok.SneakyThrows;

import java.util.List;

/**
 * <h2>DataProfileExporter</h2>
 * <summary>Exports a {@link DataProfile} with the {@link AbstractProfileStatistic} definition into a dtdl schema and
 * persists it in a json file</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 29.04.2022
 */
public class DataProfileExporter extends AbstractSchemaExporter<DataProfile> {
    private final SchemaExporter<ProfileStatistic<?, ?>> profileStatisticSchemaExporter = new ProfileStatisticsExporter();

    @SneakyThrows
    @Override
    public String export(DataProfile toExport) {
        var dtdlInterface = new DtdlInterface("dtmi:scch:at:dq:Dataprofile;1");
        dtdlInterface.setDisplayName("Data Profile");
        dtdlInterface.getContents().addAll(List.of(DtdlObject.builder()
                        .name("statistics")
                        .displayName("Profile Statistics")
                        .type("Relationship")
                        .target("dtmi:scch:at:dq:ProfileStatistics;1")
                        .build(),
                DtdlObject.builder()
                        .name("stream")
                        .target("dtmi:io:tributech:stream:base;1")
                        .type("Relationship")
                        .displayName("Profiled Stream")
                        .build())
        );
        ObjectMapper mapper = new ObjectMapper();
        var dataprofileJsonString = mapper.writeValueAsString(dtdlInterface);
        if (toExport.getStatistics() != null && !toExport.getStatistics().isEmpty()) {
            var statisticsJsonString = profileStatisticSchemaExporter.export(toExport.getStatistics().get(0));
            // ugly workaround to handle multiple definitions in one file
            dataprofileJsonString += "," + statisticsJsonString + "]";
            dataprofileJsonString = "[" + dataprofileJsonString;
        }
        return dataprofileJsonString;
    }
}
