package dqm.jku.dqmeerkat.resources.export.json.dtdl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.domain.dtdl.dto.*;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.resources.export.DataExporter;
import lombok.SneakyThrows;

import java.util.stream.Collectors;

/**
 * <h2>DtdlGraphExporter</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
public class DtdlGraphExporter implements DataExporter<DataProfile> {
    @Override
    public void export(DataProfile toExport, String filePath, String fileName) {
        throw new UnsupportedOperationException("not Implemented yet");
    }

    @SneakyThrows
    @Override
    public String export(DataProfile toExport) {
        var profileDto = DatasourceDto.builder()
                .metaData(new MetaDataDto("dtmi:scch:at:dq:Dataprofile;1"))
                .build();
        var profileDtos = toExport.getStatistics().stream().map(profileStatistic -> ProfileStatisticDto.builder()
                        .value(profileStatistic.getValue().toString())
                        .title(profileStatistic.getTitle().toString())
                        .category(profileStatistic.getCat().toString())
                        .metaData(new MetaDataDto("dtmi:scch:at:dq:ProfileStatistic;1"))
                        .build())
                .collect(Collectors.toList());
        var relationships = profileDtos.stream().map(profileStatisticDto -> RelationshipDto.builder()
                        .targetId(profileStatisticDto.getDtId())
                        .sourceId(profileDto.getDtId())
                        .relationshipName(profileStatisticDto.getTitle())
                        .build())
                .collect(Collectors.toList());
        var graph = new DtdlGraph();
        graph.addDigitalTwin(profileDto);
        profileDtos.forEach(graph::addDigitalTwin);
        relationships.forEach(graph::addRelationship);
        var graphWrapper = new DtdlGraphWrapper(graph);
        // TODO Relationships are still buggy?
        // i also uploaded a ridiculous number of profilestatistics...

        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        class NoTypes {
        }
        // without mixin jackson generates a type property for each DtdlDto implementation, causes problems with validity
        ObjectMapper mapper = new ObjectMapper().addMixIn(DtdlDto.class, NoTypes.class);

        return mapper.writeValueAsString(graphWrapper);
    }
}
