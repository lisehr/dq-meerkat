package dqm.jku.dqmeerkat.resources.export.json.dtdl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.domain.dtdl.DtdlInterface;
import dqm.jku.dqmeerkat.domain.dtdl.DtdlObject;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import lombok.SneakyThrows;

import java.util.List;

/**
 * <h2>ProfileStatisticsExporter</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 29.04.2022
 */
public class ProfileStatisticsExporter extends AbstractSchemaExporter<ProfileStatistic> {
    private final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public String export(ProfileStatistic toExport) {
        var dtdlInterface = new DtdlInterface("dtmi:scch:at:dq:ProfileStatistic;1");
        dtdlInterface.setDisplayName("Profile Statistic");
        dtdlInterface.getContents().addAll(List.of(DtdlObject.builder()
                                .name("dataProfile")
                                .displayName("Data Profile")
                                .type("Relationship")
                                .target("dtmi:scch:at:dq:Dataprofile;1")
                                .build(),
                        DtdlObject.builder()
                                .type("Property")
                                .schema("string")
                                .name("title")
                                .displayName("Statistic Title")
                                .build(),
                        DtdlObject.builder().name("value")
                                .type("Property")
                                .displayName("value")
                                .schema("string") // TODO write converter
                                .build(),
                        DtdlObject.builder()
                                .schema("string")
                                .type("Property")
                                .displayName("Statistic Category")
                                .name("category")
                                .build()
                )
        );
        return mapper.writeValueAsString(dtdlInterface);
    }
}
