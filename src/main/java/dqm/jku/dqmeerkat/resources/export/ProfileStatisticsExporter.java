package dqm.jku.dqmeerkat.resources.export;

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
public class ProfileStatisticsExporter implements AbstractExporter<ProfileStatistic> {
    @SneakyThrows
    @Override
    public void export(ProfileStatistic toExport, String fileName) {
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
        // TODO write to file
        // TODO do not write null values
        ObjectMapper mapper = new ObjectMapper();
        var ret = mapper.writeValueAsString(dtdlInterface);
        System.out.println(ret);
    }
}
